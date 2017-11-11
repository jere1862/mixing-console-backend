import socket

UDP_IP = "127.0.0.1"
UDP_PORT = 3000
MESSAGE = "Hello, World!"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))

print("Binding connection to port 3000")

while True:
    data, addr = sock.recvfrom(1024)  #buffer size 
    for i in range (0, len(data)):
        print(data[i])
    
    
#sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))
  