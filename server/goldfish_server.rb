#!/usr/bin/env ruby
require 'rubygems'
require 'eventmachine'
require 'ArgsParser'

parser = ArgsParser.parser

parser.bind :port, :p, 'default - 8931'
parser.bind :help, :h, 'show help'
@@first, @@params = parser.parse(ARGV)

{
  :port => 8931
}.each{|k,v|
  @@params[k] = v unless @@params[k]
}

@@channel = EM::Channel.new

class EchoServer < EM::Connection
  def post_init
    @sid = @@channel.subscribe{|mes|
      send_data mes
    }
    puts "new client <#{@sid}>"
    @@channel.push "new client <#{@sid}> connected\n"
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

EM::run do
  puts "GoldFish server start - port:#{@@params[:port].to_i}"
  EM::start_server('0.0.0.0', @@params[:port].to_i, EchoServer)
end
