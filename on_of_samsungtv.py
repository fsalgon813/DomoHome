import samsungctl
import time

config = {
    "name": "samsungctl",
    "description": "samsung",
    "id": "",
    "host": "192.168.0.65",
    "port": 8001,
    "method": "websocket",
    "timeout": 0,
}

with samsungctl.Remote(config) as tv:
    tv.control("KEY_POWER")