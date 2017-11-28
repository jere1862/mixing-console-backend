import socket
import argparse
import struct

parser = argparse.ArgumentParser(description='Send nodes to a server via UDP')
parser.add_argument('id', type=int, help='the node id')
parser.add_argument('type', type=str, choices=['mic','gps','micsliders'],
 help='node type')
parser.add_argument('--fix', default=False, action='store_true', help='if node is fix, otherwise mobile')
parser.add_argument('--latitude', default=45.378008, type=float, help='the node latitude (default: 45.378008)')
parser.add_argument('--longitude', default=-71.9269062, type=float, help='the node longitude (default -71.9269062)')
parser.add_argument('--hostname', type=str, default='127.0.0.1', 
                    help='the server\'s hostname (default: 127.0.0.1)')
parser.add_argument('--port', type=int, default=1337,
                    help='the server\'s port (default: 1337)')              

args = parser.parse_args()

def float_to_hex(f):
    return hex(struct.unpack('<I', struct.pack('<f', f))[0])[2:]

# Microphone data message
# FLAGS/HEADER ID0 VOL LOW MED HIGH 
#   01          05  7F   0F  0F  0F
mic_data = bytearray().fromhex('01057F0F0FFF')
# Microphone data message with slider values
# FLAGS/HEADER ID VOLSLIDER LOWSLIDER MEDSLIDER HIGHSLIDER VOLUME LOW  MED HIGH
#   02         02    7F         3D       2E         FF       05    09  FF   8C
mic_data_with_slider = bytearray().fromhex('02027F3D2EFF0509FF8C')
# Gps data message
# FLAGS/HEADER ID LAT0 LAT1 LA2 LAT3 LONG0 LONG1 LONG2 LONG3
#   03         02   00   00   00   00    11    11   11    11
gps_data = bytearray().fromhex('030542358315C28FDA93')

with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
    payload_name = ''
    payload = None
    latitude = float_to_hex(args.latitude)
    longitude = float_to_hex(args.longitude)
    if args.type == 'mic':
        payload_name = 'mic data'
        payload = mic_data
    elif args.type == 'micsliders':
        payload_name = 'mic data with sliders'
        payload = mic_data_with_slider
    elif args.type == 'gps':
        payload_name = 'gps data'
        payload = bytearray.fromhex(latitude+longitude)
        payload.insert(0, 0x00)
        payload.insert(0, 0x03)
        print(gps_data)
    if payload is not None:
        payload[0] = payload[0] + (int(args.fix) << 4)
        payload[1] = args.id
        print('Sending {} {}'.format(payload_name, payload))
        sock.sendto(payload, (args.hostname, args.port))
    else:
        print(args.type)

        