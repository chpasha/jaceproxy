#!/usr/bin/env bash

#not exists anymore
curl -v http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fttv_23136.acelive > /dev/null

#матч тв hd
curl http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fttv_1016_all.acelive > /dev/null
curl http://127.0.0.1:8000/torrent/http%3A%2F%2Fcontent.asplaylist.net%2FUHdrRURwZTJQY3hiMWY4MlpBNzJWMmxhSFVJTkZPL0YrMEF4bzlNSU1VM2lzczJEc2V1UEUzZXE5V0JMbnVOUHNWbVozQjNyU2pDZ2hVOWg5b1NMWmc9PQ%2Fcdn%2F1016_all.acelive > /dev/null
#futbol 1
curl http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fttv_625_all.acelive > /dev/null

#returns hls in old engine
curl http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fas_cid_fb9b11.acelive > /dev/null

curl -v http://127.0.0.1:8000/pid/e29a6003d845f55d49f183668f36e7c094574aa4 > /dev/null

/usr/bin/ffmpeg -loglevel quiet -t 80 -i "http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fttv_6_reg.acelive/stream.mp4" -c copy -metadata service_provider=IPTV -metadata service_name="Eurosport 2" -f mpegts
http://127.0.0.1:8000/torrent/http%3A%2F%2F91.92.66.82%2Ftrash%2Fttv-list%2Facelive%2Fttv_5_reg.acelive


torrent
curl http://127.0.0.1:8000/torrent/http%3A%2F%2Fd.rutor.info%2Fdownload%2F668268 > /dev/null

