#!/usr/bin/env ruby
require 'rubygems'
require 'eventmachine'
require 'evma_httpserver'
require 'ArgsParser'
require 'uri'
require 'json'

parser = ArgsParser.parser

parser.comment :mac_port, 'default - 8931'
parser.comment :android_port, 'default - 8930'
parser.bind :help, :h, 'show help'
@@first, @@params = parser.parse(ARGV)

{
  :mac_port => 8931,
  :android_port => 8930
}.each{|k,v|
  @@params[k] = v unless @@params[k]
}

@@channel = EM::Channel.new
@@clips = Hash.new

class MacServer < EM::Connection
  def post_init
    @sid = @@channel.subscribe{|mes|
      send_data mes
    }
    puts "new mac <#{@sid}>"
  end

  def receive_data(data)
    puts data
    return if data.strip.size < 1
    puts "<#{@sid}> #{data}"
    begin
      data = JSON.parse(data)
      if data['tag'] and data['clip']
        @@clips[data['tag']] = data['clip']
      end
    rescue => e
      STDERR.puts e
    end
  end

  def unbind
    puts "unbind <#{@sid}>"
    @@channel.unsubscribe(@sid)
  end
end

class AndroidServer  < EventMachine::Connection
  include EventMachine::HttpServer
 
  def process_http_request
    res = EventMachine::DelegatedHttpResponse.new(self)

    puts Time.now
    puts "request_method : #{@http_request_method}"
    puts "path_info : #{@http_path_info}"
    puts "query_str :"
    p query = Hash[*(@http_query_string.to_s.split('&').map{|i|
                            j = i.split('=')
                            [j[0].to_sym, URI.decode(j[1])]
                          }).flatten]

    puts "post_content :"
    p post_content = Hash[*(@http_post_content.to_s.split('&').map{|i|
                              j = i.split('=')
                              [j[0].to_sym, URI.decode(j[1])]
                            }).flatten]
    
    
    res.status = 200
    res.content = ''
    if post_content[:action] == 'copy'
      res.content = @@clips[post_content[:tag]]
    end
    res.send_response
  end
end

EM::run do
  puts "GoldFish server start"
  puts "mac server - port:#{@@params[:mac_port].to_i} - HTTP"
  EM::start_server('0.0.0.0', @@params[:mac_port].to_i, MacServer)
  puts "android server - port:#{@@params[:android_port].to_i} - TCP Socket"
  EM::start_server('0.0.0.0', @@params[:android_port].to_i, AndroidServer)
end
