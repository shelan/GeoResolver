/*
 * Copyright (c) 2015 Shelan Perera
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Created by shelan on 2/28/15.
 */

var map;

// Ban Jelačić Square - City Center
var center = new google.maps.LatLng(45.812897, 15.97706);

var geocoder = new google.maps.Geocoder();
var infowindow = new google.maps.InfoWindow();

function init() {

    var mapOptions = {
        zoom: 2,
        center: center,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    }

    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

    $.getJSON("a.json", function (data) {
        for (var i = 0; i < data.length; i++) {
            var animated = false;
            if (i == 0)
                animated = true;
            displayLocation(data[i], animated);
        }
    });
}

function displayLocation(location, is_reference) {

    var content = '<div class="infoWindow"><strong>' + location.name + '</strong>'
    '</div>';

    if (parseInt(location.lat) == 0) {
        geocoder.geocode({'address': location.address}, function (results, status) {
            if (status == google.maps.GeocoderStatus.OK) {

                var marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location,
                    title: location.name

                });

                google.maps.event.addListener(marker, 'click', function () {
                    infowindow.setContent(content);
                    infowindow.open(map, marker);

                });
            }
        });
    } else {
        var position = new google.maps.LatLng(parseFloat(location.lat), parseFloat(location.long));
        var marker = new google.maps.Marker({
            map: map,
            position: position,
            title: location.country
        });

        if (is_reference) {
            marker.setAnimation(google.maps.Animation.BOUNCE)
            marker
        }

        google.maps.event.addListener(marker, 'click', function () {
            infowindow.setContent(location.country);
            infowindow.open(map, marker);
        });
    }
}
