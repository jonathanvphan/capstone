#include <Arduino.h>
#include <U8g2lib.h>
#include <SPI.h>
#include <Wire.h>

U8G2_SSD1309_128X64_NONAME0_F_4W_SW_SPI u8g2(U8G2_R1, /* clock=*/ 13, /* data=*/ 11, /* cs=*/ 10, /* dc=*/ 9, /* reset=*/ 8);

int displayWidth = 64;
int displayHeight = 128;

// Create a U8g2log object
U8G2LOG u8g2log;

// assume 4x6 font, define width and height
#define U8LOG_WIDTH 10
#define U8LOG_HEIGHT 13

// allocate memory
uint8_t u8log_buffer[U8LOG_WIDTH*U8LOG_HEIGHT];

char sent1[30] = "this is the first sentence";
char sent2[35] = "the next thing that comes is this";
char sent3[35] = "however, this comes afterwards";
char sent4[30] = "ummmmmm";

void setup(void) {
  Serial.begin(9600);
  u8g2.begin();  
  u8g2.begin();
  u8g2.setFont(u8g2_font_profont12_tf); // choose a suitable font
  u8g2log.begin(u8g2, U8LOG_WIDTH, U8LOG_HEIGHT, u8log_buffer);  // connect to u8g2, assign buffer
  u8g2log.setLineHeightOffset(0); // set extra space between lines in pixel, this can be negative
  u8g2log.setRedrawMode(0);   // 0: Update screen with newline, 1: Update screen for every char  
}

void loop(void) {
  u8g2.setFont(u8g2_font_profont12_tf); // choose a suitable font
  int sent1len = u8g2.getStrWidth(sent1);
  int sent2len = u8g2.getStrWidth(sent2);
  int sent3len = u8g2.getStrWidth(sent3);
  int sent4len = u8g2.getStrWidth(sent4);
  Serial.println(sent1len);
  Serial.println(sent2len);
  Serial.println(sent3len);
  Serial.println(sent4len);
  u8g2.clearBuffer();          // clear the internal memory
  if (sent1len > displayWidth) {
    
  }
  u8g2.drawStr(0,10,sent1);  // write something to the internal memory
  u8g2.setCursor(50, 50);
  u8g2.sendBuffer();          // transfer internal memory to the display
  delay(2500); 
  u8g2.clearBuffer();          // clear the internal memory
  u8g2.drawStr(0,10,sent2);  // write something to the internal memory
  u8g2.setCursor(50, 50);
  u8g2.sendBuffer();          // transfer internal memory to the display
  delay(2500); 
  u8g2.clearBuffer();          // clear the internal memory
  u8g2.drawStr(0,10,sent3);  // write something to the internal memory
  u8g2.setCursor(50, 50);
  u8g2.sendBuffer();          // transfer internal memory to the display
  delay(2500); 
  u8g2.clearBuffer();          // clear the internal memory
  u8g2.drawStr(0,10,sent4);  // write something to the internal memory
  u8g2.setCursor(50, 50);
  u8g2.sendBuffer();          // transfer internal memory to the display
  delay(2500); 
  // Print a number on the U8g2log window
  u8g2log.print(sent1);
  // Print a new line, scroll the text window content if required
  // Refresh the screen
  u8g2log.print("\n");
  delay(3000);
  u8g2log.print(sent2);
  u8g2log.print("\n");
  delay(3000);
  u8g2log.print(sent3);
  u8g2log.print("\n");
  delay(3000);
  u8g2log.print(sent4);
  u8g2log.print("\n");
  delay(3000);
}
