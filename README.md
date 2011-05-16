GoldFish
========


clone this repository
---------------------

    % git clone git://github.com/shokai/goldfish.git


build and install Android app
-----------------------------

use Eclipse, or

    % cd android
    % android update project --path `pwd`
    % ant debug
    % adb install -r bin/GoldFish.apk


run mac client
--------------

    % ruby client_mac/goldfish_clipboard_client.rb --help
    % ruby client_mac/goldfish_clipboard_client.rb -tag 011a0005150dc715


run server
----------

    % ruby server/goldfish_server.rb --help
    % ruby server/goldfish_server.rb