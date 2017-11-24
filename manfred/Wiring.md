Yeah, this is a markdown file, but it's best viewed in a test editor. Sorry about that...

#Raspi
| Pin                    | Pin                    |
| ---------------------- | ---------------------- |
| 01)                    | 02)                    |
| 03) Accel:SDA (white)  | 04) Power:5V (red)     |
| 05) Accel:SCL (blue)   | 06) Power:GND (black)  |
| 07)                    | 08) GPS:RX (yellow)    |
| 09)                    | 10) GPS:TX (green)     |
| 11) LED:R              | 12)                    |
| 13) LED:G              | 14)                    |
| 15) LED:Y              | 16)                    |
| 17)                    | 18)                    |
| 19)                    | 20)                    |
| 21)                    | 22)                    |
| 23)                    | 24)                    |
| 25)                    | 26)                    |
| 27)                    | 28)                    |
| 29)                    | 30)                    |
| 31)                    | 32)                    |
| 33)                    | 34)                    |
| 35)                    | 36)                    |
| 37)                    | 38)                    |
| 39)                    | 40)                    |

#Common
3V3) left rail
5V) right rail
GND) both rails

#GPS
TX) Raspi:10
RX) Raspi:8
GND) GND
VIN) 5V

#Nano
| Pin                | Pin                |
| ------------------ | ------------------ |
| TX)                | VIN) Power:5V      |
| RX)                | GND) Power:GND     |
| RST)               | RST)               |
| GND)               | 5V)                |
| D2)                | A7)                |
| D3)                | A6)                |
| D4)                | A5) Accel:SCL      |
| D5)                | A4) Accel:SDA      |
| D6)                | A3) Servo:Rudder   |
| D7) Ultra:TRIG     | A2) Servo:Elevator |
| D8) Ultra:ECHO     | A1) Servo:Ailerons |
| D9)                | A0) Servo:Throttle |
| D10)               | REF)               |
| D11)               | 3V3) Power:3V3     |
| D12)               | D13)               |
 
#Accelerometer
VCC) 3V3
GND) GND
SCL) Raspi:05
SDA) Raspi:03

#Ultrasonic
GND) Power:GND
ECHO) Nano:D9
TRIG) Nano:D8
VCC) Power:5V

# LEDs
R) Raspi:11
GND) (220 Ohm) -> Power:GND
G) Raspi:13
Y) Raspi:15
