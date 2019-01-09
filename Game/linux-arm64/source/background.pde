class background{
  float offsetRight = 0; 
  float offsetLeft = 0 ; 
  ArrayList<PVector> wave = new ArrayList();
  
  background(){
    for(int i = 0; i < gameArea.x + gameSize.x; i++ ){
        wave.add(new PVector(i,sin(i))); 
    }
  }
  
  void drawBackground(){
    
    fill(255,255,255); 
    stroke(255,255,255);
    
    int numWaves = 5;
    float split = gameSize.x  / numWaves; 
    updateWave(offsetRight);

    for(int i = 0; i < numWaves; i++ ){
      makeWave(split * i + gameArea.x); 
    }
    
    
    updateWave(offsetLeft);
    for(int i = 0; i < numWaves; i++ ){
      makeWave(split * i + gameArea.x + split/2); 
    }
    
    offsetRight = offsetRight + 0.1; 
    offsetLeft = offsetLeft - 0.1; 
  }
  
  
  void makeWave(float offSetY){
    noFill();
    beginShape();
      for(PVector p: wave){
        
        curveVertex(gameArea.x + p.x * 10.0,offSetY+((10*p.y)));
      }
     
    endShape();
    fill(0,0,0);
  }
  
  void updateWave(float offset){
    wave.clear();
      for(int i = 0; i <= gameArea.x + gameSize.x + 1; i++ ){
        wave.add(new PVector(i,pow(2,sin(i + offset)) )); 
    }
  }
  
  
}
