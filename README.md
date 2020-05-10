# hwmonitor
Sensor monitoring on Ubuntu 20.04

This project grew out from trying to observe the various temperatures, fan speed, and power
of my machine as I was trying to tune the fan speed for optimal settings.  I have 3 sources
of sensor data:

 * `lm-sensors` gives the temperature of my Ryzen CPU,
 * `liquidctl` gives the temperature of the liquid in my NZXT Kraken X62 AIO cooler,
 * `nvidia-smi` gives GPU temperature, graphics card power draw, and fan speed
   of my NVIDIA graphics card.  It is basically able to show any data in the
    NVIDIA X Server Settings program.
 
 This project just tries to gather information from these sources.
 It uses Java Hybrid Modules System just for verification of that.
