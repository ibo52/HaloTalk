# Halotalk - Simple messaging application between LAN devices

system | specs | of development|
--- | --- | --- |
_Environment_| Debian 12| Java 17(OpenJDK)
GUI|Framework| javaFX

## compile and run
### `mvn clean javafx:run`
---
## Notes
1. Real time protocol(RTP) will be used for Video and audio streams
2. To share video over LAN on Linux derivatives, [the camera acessed by v4l2 library which comes built in linux kernel.](https://github.com/ibo52/eggs-and-childs/tree/master/socket)
3. current message management system is not suitable and manageable. A basic database will be implemented to store/retrieve the text to/from the sockets
## To Do and Features
Feature|Status| Cross platform support|Platforms tested
---| ---| ---| ---|
Find LAN Users| [ :heavy_check_mark: ]| [ :heavy_check_mark: ]| Linux, Windows
Basic User info| [ :heavy_check_mark: ]| [ :heavy_check_mark: ]| Linux, Windows
Detailed User info| [ :heavy_check_mark: ]| [ :heavy_check_mark: ]| Linux, Windows
Text messaging|[ :heavy_check_mark: ]| [ :heavy_check_mark: ]| Linux, Windows
Video Conference|[ :wavy_dash: ]|[ :heavy_minus_sign: ]|Linux

# Sample outputs of GUI
![app preview](https://github.com/ibo52/HaloTalk/blob/master/sample%20images/app-preview.png)

![app preview2](https://github.com/ibo52/HaloTalk/blob/master/sample%20images/app-preview2.png)

# VideoClient
![Video client](https://github.com/ibo52/HaloTalk/blob/master/sample%20images/video-backbone.png)