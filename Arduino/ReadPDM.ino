#include <Audio.h>
#define MICGAIN 100

// GUItool: begin automatically generated code
//AudioInputI2S i2s1;   // I2S mic
AudioInputPDM i2s1;   // PDM
AudioAnalyzePeak peak1;
AudioAnalyzeRMS rms1;
AudioConnection p1(i2s1, peak1);
AudioConnection p2(i2s1, rms1);
AudioRecordQueue         queue1;         //xy=567,690
AudioConnection          patchCord1(i2s1, queue1);
// GUItool: end automatically generated code


void setup() {
  AudioMemory(4);
  Serial.begin(100000);
  while (!Serial);
  delay(5000);
  Serial.println("RMS peak");
  queue1.begin();
}

void loop() {
  //  Serial.print(MICGAIN * rms1.read()); Serial.print(" ");
  //  Serial.println(MICGAIN * peak1.read());
  //  Serial.print(MICGAIN * rms1.read()); Serial.print(" ");
  //  Serial.println(MICGAIN * peak1.read());
  //  delay(5);
  if (queue1.available() >= 2) {
    byte buffer[512];
    // Fetch 2 blocks from the audio library and copy
    // into a 512 byte buffer.  The Arduino SD library
    // is most efficient when full 512 byte sector size
    // writes are used.
    memcpy(buffer, queue1.readBuffer(), 256);
    queue1.freeBuffer();
    memcpy(buffer + 256, queue1.readBuffer(), 256);
    queue1.freeBuffer();
    // write all 512 bytes to the SD card
    //elapsedMicros usec = 0;
    Serial.write((uint8_t*)buffer, sizeof(buffer) );
  }
}
