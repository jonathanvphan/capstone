#include <Arduino.h>
#include <U8g2lib.h>
#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h> 

SoftwareSerial MyBlue(16, 17); // RX | TX 
U8G2_SSD1309_128X64_NONAME0_F_4W_SW_SPI u8g2(U8G2_R1, /* clock=*/ 13, /* data=*/ 11, /* cs=*/ 10, /* dc=*/ 9, /* reset=*/ 8);

// Create a U8g2log object
U8G2LOG u8g2log;

// assume 4x6 font, define width and height
#define U8LOG_WIDTH1 13
#define U8LOG_HEIGHT1 16
#define U8LOG_WIDTH2 10
#define U8LOG_HEIGHT2 13
#define U8LOG_WIDTH3 9
#define U8LOG_HEIGHT3 12

// allocate memory
uint8_t u8log_buffer1[U8LOG_WIDTH1*U8LOG_HEIGHT1];
uint8_t u8log_buffer2[U8LOG_WIDTH2*U8LOG_HEIGHT2];
uint8_t u8log_buffer3[U8LOG_WIDTH3*U8LOG_HEIGHT3];
int displayWidth = 64;
int displayHeight = 128;
int fontWidthMax = 10;
unsigned long passedTime;
unsigned long clearDisplayTime = 5000;
char incomingString[200];
char character;

void setup(void) {
  Serial.begin(9600);
  MyBlue.begin(9600); 
  Serial.println("Ready to connect\nDefault password is 1234 or 000"); 
  u8g2.begin();
  u8g2.setFont(u8g2_font_profont12_tf); // choose a suitable font
  u8g2log.begin(u8g2, U8LOG_WIDTH2, U8LOG_HEIGHT2, u8log_buffer2);  // connect to u8g2, assign buffer
  u8g2log.setLineHeightOffset(0); // set extra space between lines in pixel, this can be negative
  u8g2log.setRedrawMode(0);   // 0: Update screen with newline, 1: Update screen for every char  
  passedTime = millis();
}

void changeFont(int fontSize) {
  if (fontSize == 1) {
    fontWidthMax = 13;
    u8g2.setFont(u8g2_font_profont10_tf); // choose a suitable font
    u8g2log.begin(u8g2, U8LOG_WIDTH1, U8LOG_HEIGHT1, u8log_buffer1);  // connect to u8g2, assign buffer
    u8g2log.print("Font size set to 1");
    u8g2log.println();
  } 
  else if (fontSize == 2) {
    fontWidthMax = 10;
    u8g2.setFont(u8g2_font_profont12_tf); // choose a suitable font
    u8g2log.begin(u8g2, U8LOG_WIDTH2, U8LOG_HEIGHT2, u8log_buffer2);  // connect to u8g2, assign buffer
    u8g2log.print("Font size set to 2");
    u8g2log.println();
  }
  else if (fontSize == 3) {
    fontWidthMax = 9;
    u8g2.setFont(u8g2_font_profont15_tf);
    u8g2log.begin(u8g2, U8LOG_WIDTH3, U8LOG_HEIGHT3, u8log_buffer3);  // connect to u8g2, assign buffer
    u8g2log.print("Font size set to 3");
    u8g2log.println();
  }
  u8g2log.setLineHeightOffset(0); // set extra space between lines in pixel, this can be negative
  u8g2log.setRedrawMode(0);   // 0: Update screen with newline, 1: Update screen for every char  
  passedTime = millis();
}

const char * split (const char * s, const int length) {
  // if it will fit return whole thing
  if (strlen (s) <= length)
    return s + strlen (s);

  // searching backwards, find the last space
  for (const char * space = &s [length]; space != s; space--)
    if (*space == ' ')
      return space;
    
  // not found? return a whole line
  return &s [length];        
  } // end of split

void loop(void) {
  if (Serial.available() > 0) {
    // read the incoming byte:
    Serial.readString().toCharArray(incomingString, 200);
    // Print string on the U8g2log window
    // Print a new line, scroll the text window content if required
    // Refresh the screen
    if (strcmp(incomingString, "TEST") == 0) {
      u8g2log.print("Bluetooth disconnected... reconnecting\n");
      Serial.write("A");
    }
    else if (strcmp(incomingString, "FONT1") == 0) {
      changeFont(1);
    }
    else if (strcmp(incomingString, "FONT2") == 0) {
      changeFont(2);
    }
    else if (strcmp(incomingString, "FONT3") == 0) {
      changeFont(3);
    }
    else {
      const char * p = incomingString;
  
      // keep going until we run out of text
      while (*p) {
        // find the position of the space
        const char * endOfLine = split (p, fontWidthMax);  
        
        // display up to that
        while (p != endOfLine)
          //Serial.print(*p++);
          u8g2log.print(*p++);
          
        // finish that line
        //Serial.println();
        u8g2log.println();
        
        // if we hit a space, move on past it
        if (*p == ' ')
          p++;
      }
      //u8g2log.print(incomingString);
      //u8g2log.print("\n");
    }
    passedTime = millis();
  }

  if (MyBlue.available() > 0) {
    // read the incoming byte:
    MyBlue.readString().toCharArray(incomingString, 200);
    // Print string on the U8g2log window
    // Print a new line, scroll the text window content if required
    // Refresh the screen
    Serial.print(incomingString);
    Serial.println();
    if (strcmp(incomingString, "TEST") == 0) {
      u8g2log.print("Bluetooth disconnected... reconnecting\n");
      MyBlue.write("A");
      Serial.write("A");
    }
    else if (strcmp(incomingString, "FONT1") == 0) {
      changeFont(1);
      MyBlue.write("A");
    }
    else if (strcmp(incomingString, "FONT2") == 0) {
      changeFont(2);
      MyBlue.write("A");
    }
    else if (strcmp(incomingString, "FONT3") == 0) {
      changeFont(3);
      MyBlue.write("A");
    }
    else {
      const char * p = incomingString;
  
      // keep going until we run out of text
      while (*p) {
        // find the position of the space
        const char * endOfLine = split (p, fontWidthMax);  
        
        // display up to that
        while (p != endOfLine)
          //Serial.print(*p++);
          u8g2log.print(*p++);
          
        // finish that line
        //Serial.println();
        u8g2log.println();
        
        // if we hit a space, move on past it
        if (*p == ' ')
          p++;
      }
      
      //u8g2log.print(incomingString);
      //u8g2log.print("\n");
      MyBlue.write("A");
      Serial.write("A");
    }
    passedTime = millis();
  }
      
  if (millis() - passedTime > clearDisplayTime) {
    u8g2log.print("\f");
    u8g2.clear();
    passedTime = millis();
  }
}
