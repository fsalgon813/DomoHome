import samsungctl
import time
import sys

ip = None
if(len(sys.argv) == 2):
    ip = str(sys.argv[1])


if (ip != None and ip != ""):
    config = {
        "name": "samsungctl",
        "description": "samsung",
        "id": "",
        "host": ip,
        "port": 8001,
        "method": "websocket",
        "timeout": 0,
    }

    with samsungctl.Remote(config) as tv:
        tv.control("KEY_POWER")
