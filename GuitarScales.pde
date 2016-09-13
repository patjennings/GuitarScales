import controlP5.*;
import java.util.*;
ControlP5 cp5;

boolean showGUI = false;
Slider[] sliders;
ScrollableList dropdown1, dropdown2, dropdown3, dropdown4;
String[] modes = {"Ionian - I", "Dorian - II", "Phrygian - III", "Lydian - IV", "Mixolydian - V", "Eolian - VI", "Locrian - VII"};
String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
String[] ionianNotesUpdate; // Sert à récupérer notes, mais avec un départ/offset différent eg. A, A#, B, C, C#, etc. pour la note ionienne
String[] baseNotesUpdate; // Sert à récupérer notes, mais avec un départ/offset différent eg. A, A#, B, C, C#, etc. pour la baseNote
int[] notesAvailDefault = {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1};
String ionianNote = "C";
String baseNote = "C";
String mode = modes[0];
int noteIndex;
int intervalWithBaseNote = 0;
int globalCursor = 0;
int caseWidth = 26; // largeur de case
int caseHeight = 32; // hauteur de la case

color colorBg = color(240, 240, 240);
color colorBgAlt = color(255,255,255);
color colorMain = color(38, 49, 58);
color colorComp = color(206, 209, 218);
color colorContrast = color(114, 229, 72);

// La baseNote est la note de base, le mode est le mode de la gamme choisi, et la ionianNote est la note corresponde au mode, sur un doigté ionien.

void setup(){
  size(1232, 480);
  background(0);

  setupGUI();
}

void draw(){
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
}

// Récupère l'intervalle entre la note (string) en paramètre ionianNotesUpdate, donc la liste des notes triée à partir de la BaseNote
int retrieveInterval(String noteToProcess){
  String ntp = noteToProcess;
  int value = 0;

  for(int i = 0;i<ionianNotesUpdate.length; i++){
    if(ionianNotesUpdate[i] == ntp){
      value = i;
    }
  }
  return value;
}

// crée un tableau updaté avec la nouvelle baseNote. On change la baseNote (e.g. C > F), on prend le tableau des notes notes[], on le slice à l'emplacement de la baseNote, puis on crée le nouveau tableau (e.g. C C# D D# E F F# G G# A A# B > F F# G G# A A# B C C# D D# E)
void updateNotesAndScale(){
  int bncursor = 0;
  int incursor = 0;

  for(int i = 0;i<notes.length; i++){
    if(notes[i] == ionianNote){
      bncursor = i;
    }
  }

  // On slice la tableau notes[] : n2 contient tout ce qui est après la baseNote, n1 tout ce qui est avant
  // EXEMPLE :
  // baseNote est G. On repère G > if(notes[i] == baseNote
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

int updateIntervalWithBaseNote(){

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
void drawFretboard(){

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
void drawNotes(int indexStartNote, int yPos){

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
        else if(i == 12){ // juste après le sillet
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
        else if(i == 12){ // juste après le sillet
          noFill();
          strokeWeight(2);
          stroke(colorComp);
        }
        else{ // note simple
          noStroke();
          fill(colorMain);
        }
      }

      ellipse(i*caseWidth-(caseWidth/2), yp, 16, 16); // Puis, dessin des notes

      // Active interval ? ?
      if(j == intervalWithBaseNote){ // baseNote
        noFill();
        strokeWeight(4);
        stroke(colorComp);

        if(j == 0){
          stroke(colorContrast, 125);
        }

        ellipse(i*caseWidth-(caseWidth/2), yp, 28, 28);
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

void drawFretboardPoints(){

    // Les points sur le côté du manche
    noStroke();
    fill(colorMain);
    //
    ellipse(caseWidth*15-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 3
    ellipse(caseWidth*17-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 5
    ellipse(caseWidth*19-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 7
    ellipse(caseWidth*21-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 9
    // //
    ellipse(caseWidth*24-(caseWidth/1.5), 196+(6*caseHeight), 4, 4); // case 12 gauche
    ellipse(caseWidth*24-(caseWidth/2.333), 196+(6*caseHeight), 4, 4); // case 12 droite
    // //
    ellipse(caseWidth*27-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 15
    ellipse(caseWidth*29-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 17
    ellipse(caseWidth*31-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 19
    ellipse(caseWidth*33-(caseWidth/2), 196+(6*caseHeight), 4, 4); // case 21
    // //
    ellipse(caseWidth*36-(caseWidth/1.5), 196+(6*caseHeight), 4, 4); // case 24 gauche
    ellipse(caseWidth*36-(caseWidth/2.333), 196+(6*caseHeight), 4, 4); // case 24 droite

}
