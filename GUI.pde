

void setupGUI(){

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

void drawGUI(){
  cp5.show();
  cp5.draw();
}

void controlEvent(CallbackEvent theEvent) {
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
