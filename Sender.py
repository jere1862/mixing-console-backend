import socket
 
UDP_IP = "127.0.0.1"
UDP_PORT = 1337
MESSAGE = "Hello, World!"

# Microphone data message
# FLAGS/HEADER ID0 VOL LOW MED HIGH 
#   01          05  7F   0F  0F  0F
mic_data = bytearray().fromhex('01057F0F0F0F')
# Microphone data message with slider values
# FLAGS/HEADER ID VOLSLIDER LOWSLIDER MEDSLIDER HIGHSLIDER LOW  MED HIGH
#   02         02    7F         3D       2E         FF      09  FF   8C
mic_data_with_slider = bytearray().fromhex('02027F3D2EFF09FF8C')
# Gps data message
# FLAGS/HEADER ID LAT0 LAT1 LA2 LAT3 LONG0 LONG1 LONG2 LONG3
#   03         02   00   00   00   00    11    11   11    11
gps_data = bytearray().fromhex('03020000000011111111')

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP

print('Accepted values: [mic, micsliders, gps, exit]')

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    while True:
        s = input('-->')
        if s == 'exit': break
        if s == 'mic':
            payload_name = 'mic data'
            payload = mic_data
        elif s == 'micsliders':
            payload_name = 'mic data with sliders'
            payload = mic_data_with_slider
        elif s == 'gps':
            payload_name = 'gps data'
            payload = gps_data
        else: break
        print('Sending {}'.format(payload))
        sock.sendto(gps_data, (UDP_IP, UDP_PORT))
