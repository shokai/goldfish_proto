#!/usr/bin/env ruby
require 'rubygems'
require 'eventmachine'
require 'evma_httpserver'
require 'ArgsParser'

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

class MacServer < EM::Connection
  def post_init
    @sid = @@channel.subscribe{|mes|
      send_data mes
    }
    puts "new mac <#{@sid}>"
    @@channel.push "new mac <#{@sid}> connected\n"
  end

  def receive_data(data)
    puts data
    return if data.strip.size < 1
    puts "<#{@sid}> #{data}"
    send_data "echo to <#{@sid}> : #{data}\n"
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
    puts "query_str : #{@http_query_string}"
    puts "post_content : #{@http_post_content}"

    res.status = 200
    res.content = "android server"
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
