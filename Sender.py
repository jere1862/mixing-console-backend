import socket
 
UDP_IP = "127.0.0.1"
UDP_PORT = 1337
MESSAGE = "Hello, World!"

# Microphone data message
# ID0 ID1 FLAGS VOL LOW MED HIGH 
#  0   1    0   7F   0F  0F  0F
mic_data = bytearray().fromhex('0001007F0F0F0F')
# Gps data message
# ID0 ID1 LAT0 LAT1 LAT2 LAT3 LONG0 LONG1 LONG2 LONG3
#  0   2   00   00   00   00    11    11   11    11
gps_data = bytearray().fromhex('0002000000001111111100')

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
                     
sock.sendto(gps_data, (UDP_IP, UDP_PORT))

sock.close()