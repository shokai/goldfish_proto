#!/usr/bin/env ruby
require 'rubygems'
require 'eventmachine'
require 'ArgsParser'
require 'json'

parser = ArgsParser.parser
parser.bind :server, :s, 'default - dev.shokai.org'
parser.bind :port, :p, 'default - 8931'
parser.comment :tag, 'tag id'
parser.bind :help, :h, 'show help'
@@first, @@params = parser.parse(ARGV)

{
  :server => 'dev.shokai.org',
  :port => 8931,
}.each{|k,v|
  @@params[k] = v unless @@params[k]
}

if parser.has_option(:help) or !parser.has_param(:tag)
  puts parser.help
  exit
end

p @@params

RECONNECT_INTERVAL = 5

class GoldFishClient < EM::Connection
  def post_init
    send_data({:tag => @@params[:tag]}.to_json)
    EM::defer do
      loop do
        clip = `pbpaste`
        if clip.size > 0
          if @clip != clip
            @clip = clip
            puts "send clipboard : #{@clip}"
            send_data({
                        :clip => @clip,
                        :tag => @@params[:tag]
                      }.to_json)
          end
        end
        sleep 1
      end
    end
  end

  def receive_data(data)
    return if data.strip.size < 1
    data.gsub!(/'/,'\'')
    puts data
    begin
      data = JSON.parse(data)
    rescue => e
      STDERR.puts e
    end
    if data['clip']
      if data['clip'] =~ /^https?:\/\/.+/
        `open '#{data['clip']}'`
      else
        `echo '#{data['clip']}' | pbcopy`
      end
    end
  end

  def unbind
    puts "connection closed - #{@@params[:server]}:#{@@params[:port]}"
    EM::add_timer(RECONNECT_INTERVAL) do
      reconnect(@@params[:server], @@params[:port].to_i)
    end
  end
end

EM::run do
  EM::connect(@@params[:server], @@params[:port].to_i, GoldFishClient)
end
