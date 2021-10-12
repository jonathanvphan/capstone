#include <Audio.h>
#define MICGAIN 100

// GUItool: begin automatically generated code
//AudioInputI2S i2s1;   // I2S mic
AudioInputPDM i2s1;   // PDM
AudioAnalyzePeak peak1;
AudioAnalyzeRMS rms1;
AudioConnection p1(i2s1, peak1);
AudioConnection p2(i2s1, rms1);
// GUItool: end automatically generated code


void setup() {
  AudioMemory(4);
  Serial.begin(9600);
  while(!Serial);
  delay(5000);
  Serial.println("RMS peak");
}

void loop() {
//  Serial.print(MICGAIN * rms1.read()); Serial.print(" ");
//  Serial.println(MICGAIN * peak1.read());
  Serial.print(MICGAIN * rms1.read()); Serial.print(" ");
  Serial.println(MICGAIN * peak1.read());
  delay(5);
}
