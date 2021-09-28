#include <Arduino.h>
#include <U8g2lib.h>
#include <SPI.h>
#include <Wire.h>

U8G2_SSD1309_128X64_NONAME0_F_4W_SW_SPI u8g2(U8G2_R0, /* clock=*/ 13, /* data=*/ 11, /* cs=*/ 10, /* dc=*/ 9, /* reset=*/ 8);

int displayWidth = 128;
int displayHeight = 64;
char sent1[30] = "this is the first sentence";
char sent2[35] = "the next thing that comes is this";
char sent3[35] = "however, this comes afterwards";
char sent4[30] = "ummmmmm";

void setup(void) {
  Serial.begin(9600);
  u8g2.begin();  
}

void loop(void) {
  u8g2.setFont(u8g2_font_ncenB08_tr); // choose a suitable font
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
}
