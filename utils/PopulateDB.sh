#!/bin/sh

echo "Sending nodes to the server..."

python Sender.py 1 mic --fix 
python Sender.py 1 gps --latitude 45.378008 --longitude -71.9269062

python Sender.py 2 mic
python Sender.py 2 gps --latitude 45.378478 --longitude -71.9277089

python Sender.py 3 mic
python Sender.py 3 micsliders
python Sender.py 3 gps --latitude 45.378248 --longitude -71.9274240

echo "Sent data to the server."

exec $SHELL
