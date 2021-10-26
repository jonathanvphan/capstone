#include <Arduino.h>
#include <U8g2lib.h>
#include <SPI.h>
#include <Wire.h>

U8G2_SSD1309_128X64_NONAME0_F_4W_SW_SPI u8g2(U8G2_R0, /* clock=*/ 13, /* data=*/ 11, /* cs=*/ 10, /* dc=*/ 9, /* reset=*/ 8);

// Create a U8g2log object
U8G2LOG u8g2log;

// assume 4x6 font, define width and height
#define U8LOG_WIDTH 20
#define U8LOG_HEIGHT 6

// allocate memory
uint8_t u8log_buffer[U8LOG_WIDTH*U8LOG_HEIGHT];

int displayWidth = 128;
int displayHeight = 64;
String incomingString = "";

void setup(void) {
  Serial.begin(9600);
  u8g2.begin();
  u8g2.setFont(u8g2_font_ncenB08_tr); // choose a suitable font
  u8g2log.begin(u8g2, U8LOG_WIDTH, U8LOG_HEIGHT, u8log_buffer);  // connect to u8g2, assign buffer
  u8g2log.setLineHeightOffset(0); // set extra space between lines in pixel, this can be negative
  u8g2log.setRedrawMode(0);   // 0: Update screen with newline, 1: Update screen for every char  
}

void loop(void) {
  if (Serial.available() > 0) {
    // read the incoming byte:
    incomingString = Serial.readString();
    // Print string on the U8g2log window
    // Print a new line, scroll the text window content if required
    // Refresh the screen
    u8g2log.print(incomingString);
    u8g2log.print("\n");
  }
}
