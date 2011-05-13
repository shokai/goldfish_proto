#!/usr/bin/env ruby
# -*- coding: utf-8 -*-
require 'rubygems'
require 'eventmachine'
require 'evma_httpserver'

port = 8930
port = ARGV.shift.to_i if ARGV.size > 0

class Handler  < EventMachine::Connection
  include EventMachine::HttpServer
 
  def process_http_request
    res = EventMachine::DelegatedHttpResponse.new(self)

    puts Time.now
    puts "request_method : #{@http_request_method}"
    puts "path_info : #{@http_path_info}"
    puts "query_str : #{@http_query_string}"
    puts "post_content : #{@http_post_content}"

    res.status = 200
    res.content = "<h1>debug server</h1>hello debug server"
    res.send_response
  end
end

EventMachine::run do
  EventMachine::start_server("0.0.0.0", port, Handler)
  puts "http server start, port #{port}"
end
