#Raspi
| Pin              | Pin               |
| ---------------- | ----------------- |
| 01) GPS:3.3V     | 02)               |
| 03) Accel:SDA    | 04) Power:5V      |
| 05) Accel:SCL    | 06) Power:GND     |
| 07)              | 08) GPS:RX        |
| 09) Accel:GND    | 10) GPS:TX        |
| 11) LED:R        | 12)               |
| 13) LED:G        | 14) GPS:GND       |
| 15) LED:B        | 16)               |
| 17) Accel:VCC    | 18)               |
| 19)              | 20)               |
| 21)              | 22)               |
| 23)              | 24)               |
| 25)              | 26)               |
| 27)              | 28)               |
| 29)              | 30)               |
| 31)              | 32)               |
| 33)              | 34)               |
| 35)              | 36)               |
| 37)              | 38)               |
| 39)              | 40)               |

#Common
3V3)
5V)
GND)

#GPS
TX) Raspi:10
RX) Raspi:8
GND) Power:GND
VIN) Power:3V3

#Nano
| Pin                | Pin                |
| ------------------ | ------------------ |
| TX)                | VIN) Power:5V      |
| RX)                | GND) Power:GND     |
| RST)               | RST)               |
| GND)               | 5V)                |
| D2)                | A7)                |
| D3)                | A6)                |
| D4)                | A5) Raspi:SCL      |
| D5)                | A4) Raspi:SDA      |
| D6)                | A3) Servo:Throttle |
| D7) Ultra:TRIG     | A2) Servo:Ailerons |
| D8) Ultra:ECHO     | A1) Servo:Elevator |
| D9)                | A0) Servo:Rudder   |
| D10)               | REF)               |
| D11)               | 3V3)               |
| D12)               | D13)               |
 
#Accelerometer
VCC) Raspi:17
GND) Raspi:09
SCL) Raspi:05
SDA) Raspi:03

#Ultrasonic
GND) Power:GND
ECHO) Nano:D9
TRIG) Nano:D8
VCC) Power:5V

# LEDs
R) Raspi:11
GND) (resistor) -> Power:GND
G) Raspi:13
Y) Raspi:15
