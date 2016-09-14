import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GuitarScales extends PApplet {



ControlP5 cp5;

boolean showGUI = false;
Slider[] sliders;
ScrollableList dropdown1, dropdown2, dropdown3, dropdown4;
String[] modes = {"Ionian - I", "Dorian - II", "Phrygian - III", "Lydian - IV", "Mixolydian - V", "Eolian - VI", "Locrian - VII"};
String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
String[] ionianNotesUpdate; // Sert \u00e0 r\u00e9cup\u00e9rer notes, mais avec un d\u00e9part/offset diff\u00e9rent eg. A, A#, B, C, C#, etc. pour la note ionienne
String[] baseNotesUpdate; // Sert \u00e0 r\u00e9cup\u00e9rer notes, mais avec un d\u00e9part/offset diff\u00e9rent eg. A, A#, B, C, C#, etc. pour la baseNote
int[] notesAvailDefault = {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1};
String ionianNote = "C";
String baseNote = "C";
String mode = modes[0];
int noteIndex;
int intervalWithBaseNote = 0;
int globalCursor = 0;
int caseWidth = 26; // largeur de case
int caseHeight = 32; // hauteur de la case
int dotSize = 14;
int ellipseSize = 24;

int colorBg = color(235, 235, 235);
int colorBgAlt = color(255,255,255);
int colorMain = color(38, 49, 58);
int colorComp = color(206, 209, 218);
int colorContrast = color(114, 229, 72);

// La baseNote est la note de base, le mode est le mode de la gamme choisi, et la ionianNote est la note corresponde au mode, sur un doigt\u00e9 ionien.

public void setup(){
  
  background(colorMain);

  setupGUI();
}

public void draw(){
  background(colorBg);

  // Notes processing
  updateNotesAndScale();
  intervalWithBaseNote = updateIntervalWithBaseNote();

  // Draw guitar
  drawFretboard();
  drawFretboardPoints();

  // Draw notes on each string
  drawNotes(retrieveInterval("E"), 356); // E string
  drawNotes(retrieveInterval("A"), 324); // A string
  drawNotes(retrieveInterval("D"), 292); // D string
  drawNotes(retrieveInterval("G"), 260); // G string
  drawNotes(retrieveInterval("B"), 228); // B string
  drawNotes(retrieveInterval("E"), 196); // E string

  drawGUI();
  guiComplements();
}

public void guiComplements(){


  // L\u00e9gende \u00e0 c\u00f4t\u00e9 des champs qui contr\u00f4lent le sketch
  if(mode == modes[0]){
    // baseNote (mode ionien)
    noFill();
    strokeWeight(4);
    stroke(colorContrast, 125);
    ellipse(312, 72, ellipseSize, ellipseSize);

    noStroke();
    fill(colorContrast);
    ellipse(312, 72, dotSize, dotSize);

    // ionian note
    noStroke();
    fill(colorContrast);
    ellipse(616, 72, dotSize, dotSize);
  }
  else{
    // baseNote (mode ionien)
    noFill();
    strokeWeight(4);
    stroke(colorComp);
    ellipse(312, 72, ellipseSize, ellipseSize);

    noStroke();
    fill(colorMain);
    ellipse(312, 72, dotSize, dotSize);

    // ionian note
    noStroke();
    fill(colorContrast);
    ellipse(616, 72, dotSize, dotSize);
  }

}

// R\u00e9cup\u00e8re l'intervalle entre la note (string) en param\u00e8tre ionianNotesUpdate, donc la liste des notes tri\u00e9e \u00e0 partir de la BaseNote
public int retrieveInterval(String noteToProcess){
  String ntp = noteToProcess;
  int value = 0;

  for(int i = 0;i<ionianNotesUpdate.length; i++){
    if(ionianNotesUpdate[i] == ntp){
      value = i;
    }
  }
  return value;
}

// cr\u00e9e un tableau updat\u00e9 avec la nouvelle baseNote. On change la baseNote (e.g. C > F), on prend le tableau des notes notes[], on le slice \u00e0 l'emplacement de la baseNote, puis on cr\u00e9e le nouveau tableau (e.g. C C# D D# E F F# G G# A A# B > F F# G G# A A# B C C# D D# E)
public void updateNotesAndScale(){
  int bncursor = 0;
  int incursor = 0;

  for(int i = 0;i<notes.length; i++){
    if(notes[i] == ionianNote){
      bncursor = i;
    }
  }

  // On slice la tableau notes[] : n2 contient tout ce qui est apr\u00e8s la baseNote, n1 tout ce qui est avant
  // EXEMPLE :
  // baseNote est G. On rep\u00e8re G > if(notes[i] == baseNote
  String[] bn2 = subset(notes, bncursor);
  String[] bn1 = subset(notes, 0, bncursor);

  ionianNotesUpdate = concat(bn2, bn1);

  for(int i = 0;i<notes.length; i++){
    if(notes[i] == baseNote){
      incursor = i;
    }
  }

  String[] in2 = subset(notes, incursor);
  String[] in1 = subset(notes, 0, incursor);

  baseNotesUpdate = concat(in2, in1);

}

public int updateIntervalWithBaseNote(){

  int interval = 0;

  if(mode == modes[0]){
    interval = 0;
  }
  else if(mode == modes[1]){
    interval = 2;
  }
  else if(mode == modes[2]){
    interval = 4;
  }
  else if(mode == modes[3]){
    interval = 5;
  }
  else if(mode == modes[4]){
    interval = 7;
  }
  else if(mode == modes[5]){
    interval = 9;
  }
  else if(mode == modes[6]){
    interval = 11;
  }

  return interval;
}
// Dessin du manche de la guitare
public void drawFretboard(){

  // Fond du fretboard actif
  noStroke();
  fill(colorBgAlt);
  rect(caseWidth*12, 197, 962, 160);

  // Strings
  for(int i = 0; i < 6; i++){
    noFill();
    strokeWeight(2);
    stroke(colorComp);
    line(0, 196+(i*caseHeight), width, 196+(i*caseHeight));
  }

  // Frets
  for(int i = 1; i < 48; i++){
    noFill();
    strokeWeight(2);
    stroke(colorComp);

    if(i == 12){ // Sillet
      strokeWeight(5);
      stroke(colorMain);
    }

    line(i*caseWidth, 196, i*caseWidth, 356);
  }
}

// Dessin des notes
public void drawNotes(int indexStartNote, int yPos){

  int isn = indexStartNote;
  int yp = yPos;
  int j = isn;
  int t = 0;
  int alpha = 255;

  for(int i = 0; i < 48; i++){

    // Is the note on the scale. If so, display it.
    if(notesAvailDefault[j] == 1){
      if(i <= 12 || i > 36){ // BEFORE AND AFTER FRETBOARD
        // Key note ?
        if(j == 0){ // ionianNote
          noStroke();
          fill(colorContrast);
          //ellipse(i*26-13, yp, 16, 16);
        }
        else if(i == 12){ // juste apr\u00e8s le sillet
          noFill();
          strokeWeight(2);
          stroke(colorComp);
        }
        else{ // note simple
          noStroke();
          fill(colorComp);
        }
      }
      else{ // NOTES INTO FRETBOARD
        // Key note ?
        if(j == 0){ // ionianNote
          noStroke();
          fill(colorContrast);
        }
        else if(i == 12){ // juste apr\u00e8s le sillet
          noFill();
          strokeWeight(2);
          stroke(colorComp);
        }
        else{ // note simple
          noStroke();
          fill(colorMain);
        }
      }

      ellipse(i*caseWidth-(caseWidth/2), yp, dotSize, dotSize); // Puis, dessin des notes

      // Active interval ? ?
      if(j == intervalWithBaseNote){ // baseNote
        noFill();
        strokeWeight(4);
        stroke(colorComp);

        if(j == 0){
          stroke(colorContrast, 125);
        }

        ellipse(i*caseWidth-(caseWidth/2), yp, ellipseSize, ellipseSize);
      }
    }

    if(j == 11){
      j = 0;
      t++;
    } else {
      j++;
    }
  }
}

public void drawFretboardPoints(){

    // Les points sur le c\u00f4t\u00e9 du manche
    noStroke();
    fill(colorMain);
    //
    ellipse(caseWidth*15-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 3
    ellipse(caseWidth*17-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 5
    ellipse(caseWidth*19-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 7
    ellipse(caseWidth*21-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 9
    // //
    ellipse(caseWidth*24-(caseWidth/1.5f), 196+(6*caseHeight), 4, 4); // case 12 gauche
    ellipse(caseWidth*24-(caseWidth/2.333f), 196+(6*caseHeight), 4, 4); // case 12 droite
    // //
    ellipse(caseWidth*27-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 15
    ellipse(caseWidth*29-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 17
    ellipse(caseWidth*31-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 19
    ellipse(caseWidth*33-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 21
    // //
    ellipse(caseWidth*36-(caseWidth/1.5f), 196+(6*caseHeight), 4, 4); // case 24 gauche
    ellipse(caseWidth*36-(caseWidth/2.333f), 196+(6*caseHeight), 4, 4); // case 24 droite

}


public void setupGUI(){

  cp5 = new ControlP5(this);
  PFont myFont = createFont("Georgia",20);

  CColor c = new CColor();
  c.setBackground(colorMain);

  List baseNotesList = Arrays.asList(notes);
  List ionianNotesList = Arrays.asList(notes);
  List modesList = Arrays.asList(modes);
  List referenceDegreeList = Arrays.asList("Ionian note", "Eolian/Minor note");
  /* add a ScrollableList, by default it behaves like a DropdownList */
  dropdown1 = cp5.addScrollableList("baseNotes")
     .setPosition(336, 64)
     .setSize(96, 100)
     .setBarHeight(16)
     .setItemHeight(16)
     .addItems(baseNotesList)
     .setColor(c)
     .setType(ScrollableList.DROPDOWN) // currently supported DROPDOWN and LIST
     .setCaptionLabel(baseNote) // Default value, is replaced when dropdown is clicked
     .close()
     ;
  dropdown2 = cp5.addScrollableList("Modes")
     .setPosition(448, 64)
     .setSize(128, 100)
     .setBarHeight(16)
     .setItemHeight(16)
     .addItems(modesList)
     .setColor(c)
     .setType(ScrollableList.DROPDOWN) // currently supported DROPDOWN and LIST
     .setCaptionLabel(modes[0]) // Default value, is replaced when dropdown is clicked
     .close()
     ;
   dropdown3 = cp5.addScrollableList("ionianNotes")
      .setPosition(632, 64)
      .setSize(50, 100)
      .setBarHeight(16)
      .setItemHeight(16)
      .addItems(ionianNotesList)
      .setColor(c)
      .setType(ScrollableList.DROPDOWN) // currently supported DROPDOWN and LIST
      .setCaptionLabel(ionianNote) // Default value, is replaced when dropdown is clicked
      .close()
      ;
  cp5.addTextlabel("dd1Label")
      .setText("Base Note")
      .setPosition(336,32)
      .setColorValue(colorMain)
      ;
  cp5.addTextlabel("dd2Label")
      .setText("Mode")
      .setPosition(448,32)
      .setColorValue(colorMain)
      ;
  cp5.addTextlabel("dd3Label")
      .setText("Ionian note")
      .setPosition(632,32)
      .setColorValue(colorMain)
      ;
}

public void drawGUI(){
  cp5.show();
  cp5.draw();
}

public void controlEvent(CallbackEvent theEvent) {
  if (theEvent.getController().equals(dropdown1)) {
    switch(theEvent.getAction()) {
      case(ControlP5.ACTION_RELEASED):
        int val1 = floor(dropdown1.getValue());
        String currentVal1 = cp5.get(ScrollableList.class, "baseNotes").getItem(val1).get("text").toString();
        baseNote = currentVal1;
        updateNotesAndScale();

        if(mode != modes[0]){ // si pas ionian mode
          int c = (baseNotesUpdate.length)-updateIntervalWithBaseNote();
          ionianNote = baseNotesUpdate[c];
        }
        else{
          ionianNote = baseNote;
        }
        // Actualise ionanMode controller avec la bonne note
        for(int i = 0; i<notes.length; i++){
          if(notes[i] == ionianNote){
            dropdown3.setValue(i);
          }
        }
      break;
    }
  }
  if (theEvent.getController().equals(dropdown2)) {
    switch(theEvent.getAction()) {
      case(ControlP5.ACTION_RELEASED):
        int val2 = floor(dropdown2.getValue());
        String currentVal2 = cp5.get(ScrollableList.class, "Modes").getItem(val2).get("name").toString();
        mode = currentVal2;

        if(mode != modes[0]){ // si pas ionian mode
          int c = (baseNotesUpdate.length)-updateIntervalWithBaseNote();
          ionianNote = baseNotesUpdate[c];
        }
        else{
          ionianNote = baseNote;
        }

        // Actualise ionanMode controller avec la bonne note
        for(int i = 0; i<notes.length; i++){
          if(notes[i] == ionianNote){
            dropdown3.setValue(i);
          }
        }
      break;
    }
  }
  if (theEvent.getController().equals(dropdown3)) {
    switch(theEvent.getAction()) {
      case(ControlP5.ACTION_RELEASED):
        int val3 = floor(dropdown3.getValue());
        String currentVal3 = cp5.get(ScrollableList.class, "ionianNotes").getItem(val3).get("text").toString();
        ionianNote = currentVal3;
        updateNotesAndScale();

        if(mode != modes[0]){ // si pas ionian mode
          int c = updateIntervalWithBaseNote();
          baseNote = ionianNotesUpdate[c];
        }
        else{
          baseNote = ionianNote;
        }

        // Actualise ionanMode controller avec la bonne note
        for(int i = 0; i<notes.length; i++){
          if(notes[i] == baseNote){
            dropdown1.setValue(i);
          }
        }
      break;
    }
  }
}
  public void settings() {  size(1232, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "GuitarScales" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
