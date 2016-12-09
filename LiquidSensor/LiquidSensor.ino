// Liquid Level Sensor Sketch

// Show the raw resistance values measured from an eTape liquid level sensor.
// See details on the sensor at: https://www.adafruit.com/products/463

// http://opensource.org/licenses/MIT

// Configuration values:
#define SERIES_RESISTOR     3450    // Value of potentiometer used to divide 3.3V down to 0-1V range    
#define SENSOR_PIN          0      // Analog pin with ~1V maximum voltage 

#define ZERO_VOLUME_RESISTANCE    7287.67    // Resistance value when bottle is empty.
#define CALIBRATION_RESISTANCE    623.64    // Resistance value when bottle is filled to 32oz.
#define CALIBRATION_VOLUME        32.00    // Volume in fl oz when liquid is at max line.
 
void setup(void) {
  Serial.begin(115200);
}
 
void loop(void) {
  // Measure sensor resistance.
  float resistance = readResistance(SENSOR_PIN, SERIES_RESISTOR);
  Serial.print("Resistance: "); 
  Serial.print(resistance, 2);
  Serial.println(" ohms");
  // Map resistance to volume.
  float volume = resistanceToVolume(resistance, ZERO_VOLUME_RESISTANCE, CALIBRATION_RESISTANCE, CALIBRATION_VOLUME);
  Serial.print("Calculated volume: ");
  Serial.println(volume, 5);
  delay(5000);
}

float readResistance(int pin, int seriesResistance) {
  // Get ADC value.
  float resistance = analogRead(pin);
  // Convert ADC reading to resistance.
  resistance = (1023.0 / resistance) - 1.0;
  resistance = seriesResistance / resistance;
  return resistance;
}

float resistanceToVolume(float resistance, float zeroResistance, float calResistance, float calVolume) {
  if (resistance > zeroResistance || (zeroResistance - calResistance) == 0.0) {
    // Stop if the value is above the zero threshold, or no max resistance is set (would be divide by zero).
    return 0.0;
  }
  // Compute scale factor by mapping resistance to 0...1.0+ range relative to maxResistance value.
  float scale = (zeroResistance - resistance) / (zeroResistance - calResistance);
  // Scale maxVolume based on computed scale factor.
  return calVolume * scale;
}
