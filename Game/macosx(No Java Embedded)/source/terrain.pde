class terrain{
  ArrayList<PVector> verteces = new ArrayList<PVector> ();
  int numberVerteces;
  PVector averageVertex; 
   boolean toColor= true;
     boolean lightUp = false;
  PVector[] edges;
  terrain(){
    numberVerteces =0;
  }
  
  void getAverageVertex(){
    float  x = 0; 
    float y = 0; 
    for(int i = 0; i < numberVerteces; i++){
      x = x + verteces.get(i).x;
      y = y + verteces.get(i).y;
    }
    averageVertex = new PVector(x/numberVerteces, y/numberVerteces);
  }
  
  void drawShape(){
    if(!toColor){
      return;
    }
    fill(34,139,34);
    
    if(lightUp){
        fill(255,255,255);
    }
    lightUp = false;
    //strokeWeight(2);
    //noStroke();
    //stroke(255,255,255);
    beginShape();
    for(int i = 0; i< verteces.size();i++){
      PVector v = verteces.get(i);
      vertex(v.x,v.y);
    }
    endShape(CLOSE);  
    stroke(0,0,0);
    fill(0,0,0);
    strokeWeight(1);
    
  }
  
  // Orders verteces around a central point using merge sort algorithm 
  void orderVerteces(int start, int end ){
    if(start < end) {

      int middle = (start + end )/2;

      orderVerteces( start, middle);  
      orderVerteces( middle + 1, end);
      mergeSort(start, middle, end);
    }
  }
  
  // Merge sort algorithm 
  void  mergeSort( int start, int middle, int end){
    ArrayList<PVector> temp = new ArrayList<PVector>();
    int i = start; 
    int j = middle + 1; 
    while(i<= middle && j <= end) {
      if( getAngle(verteces.get(i)) <= getAngle(verteces.get(j))){
        temp.add(verteces.get(i));
        i++;
      }
      else{
        temp.add(verteces.get(j));
        j++; 
      }
    }
    
    while(i<=  middle) {
      temp.add(verteces.get(i));
      i++;
    }
    
    while(j<= end){
      temp.add(verteces.get(j));
      j++;
    }

    for( int k = 0; k < temp.size(); k++){
      
      verteces.set(start + k,temp.get(k));
    }
    
  }
  
  float getAngle(PVector point){
    PVector diff = PVector.sub(point, averageVertex);
     float an = atan2(diff.y,diff.x); 
     return an; 
  }
  
}
