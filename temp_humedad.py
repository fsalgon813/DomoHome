import Adafruit_DHT
import psutil

# Primero, comprobamos si el proceso libgpiod esta ejecutandose, si es asi, lo mata
# El proceso libgpiod es un proceso escrito en C que interactua con los dispositivos GPIO(por ejemplo, los sensores conectados a los pines) en Linux
for proc in psutil.process_iter():
    if proc.name() == 'libgpiod_pulsein' or proc.name() == 'libgpiod_pulsei':
        proc.kill

# Creamos una variable que contiene el pin usado
pin = 4

# Le asignamos el tipo sensor a la variable sensor
sensor = Adafruit_DHT.DHT11

# Leemos la temperatura y humedad
humedad, temp = Adafruit_DHT.read_retry(sensor, pin)
# Si la temperatura y la humedad no son nulas, la imprimimos como si fuera un csv(separadas por ;) siendo primero la temperatura y despues la humedad
if humedad is not None and temp is not None:
        print("{0:0.1f};{1:0.1f}".format(temp, humedad))
else:
        # Si no a podido leer la temperatura, devuelve la temperatura en -1(Elijo -1 porque la temperatura minima que mide el sensor es 0ºC)
        print("-1.0;-1.0")