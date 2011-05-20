// ==UserScript==
// @name           goldfish
// @namespace      http://masui.sfc.keio.ac.jp/
// @description    send URL
// @include        http://*
// @include        https://*
// @version        0.0.2
// @author         Sho Hashimoto <hashimoto@shokai.org>
// ==/UserScript==

var goldfish = function(){
    // var api_url = "http://localhost:8930";
    var api_url = "http://dev.shokai.org:8930";

    var nfc_tag = "011a0005150dc715"; // shokai
    // var nfc_tag = "535209c7"; // shokai air
    // var nfc_tag = "d36309c7";
    // var nfc_tag = "234609c7";
    // var nfc_tag = "535209c7";

    GM_xmlhttpRequest({
        method: "POST",
        url: api_url+'/browser',
        data: "url="+encodeURIComponent(location.href) +
            "&title="+encodeURIComponent(document.title) +
            "&tag="+encodeURIComponent(nfc_tag),
        onload: function(response) {
        }
    });
}();
