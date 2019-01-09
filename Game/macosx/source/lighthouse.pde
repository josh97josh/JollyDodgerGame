class lighthouse{
  float angle,size,lastShip,shipGenerationRate;
  PVector c;
  PVector pos = new PVector(384, 384);
  
  lighthouse(){
    angle = 0 ;
    size = PI/2;
    lastShip = 0; 
    shipGenerationRate = 5000; 
    if(gameSize != null){
      pos = new PVector(gameArea.x + (gameSize.x)/2, gameArea.y + (gameSize.y)/2);
    }
    
  }
  
  // Adds rays for enemies, islands and player and draw lighthouse
    void create(ArrayList<ray> newRay, ArrayList< enemy> circles, ArrayList< island> tr ){
     if(!gamePaused){
      angle = angle + 0.01 ; 
     }
      if(angle > PI){
        angle -= TWO_PI;
      }
      ArrayList<PVector> shadowVerteces = new ArrayList<PVector>();
      
      for(ray r: newRay){
        shadowVerteces.addAll(r.getAnglesForShape(tr,enemies));
      }
    
      for(ray r: newRay){
        shadowVerteces.addAll(r.getAnglesForCircle(tr,enemies));
      }
    
    ray light = addEdges();
    shadowVerteces.addAll(light.getAnglesForShape(tr,circles));

    detectPlayer();
    
    shadow sd = new shadow(shadowVerteces,angle,size);
    sd.drawShape();
    lhimg.resize(50,100);
    image(lhimg,pos.x-gameSize.x/39,pos.y - gameSize.y/100);
  }
  
  // Add beam edges to draw in shine in set direction 
  ray addEdges(){
    ray r = new ray(new ArrayList<island>(), new ArrayList<enemy>(),pl);
    PVector d = new PVector(cos(angle), sin(angle)) ;
    ArrayList< PVector> toAdd = new ArrayList<PVector>() ;
    toAdd.add(r.findRays(d,0,pos));
    d = new PVector(cos(angle + size),sin(angle + size)) ; 
    toAdd.add(r.findRays(d,1,pos));
    island is = new island(toAdd);
    ray light = new ray(tr,is,pl);
    return light; 
  }
  
  // Checks whether the player has been detected by the lighbeam and if so adds a new enemy 
  void detectPlayer(){
        ray r2 = new ray(tr,enemies,pl);
      PVector toPlayer = PVector.sub(pl.pos,pos);
      
      r2.findRays(toPlayer,2,pos);
      
      if(r2.hitCircle != null){
      if( r2.hitCircle.pos.x == pl.pos.x && r2.hitCircle.pos.y == pl.pos.y && isContained(atan2(toPlayer.y,toPlayer.x))){
        if(!pl.detected){
          warning.loop();
          pl.detected = true;
        }

          if(canCreateNewShip()){
            enemy added = new enemy(sg.getSpawnLocation(),new PVector(random(-3,3),random(-3,3)),30);
            enemies.add(added);
            newRay.add(new ray(tr,added,pl));
            numBalls ++;
          }
      }
      else{
        if(pl.detected){
          warning.pause(); 
          warning.rewind();
          pl.detected = false; 
        }
      }
    }
    
  }
  
  boolean isContained(float ag){
    if(ag > angle){
      return (ag - angle) <= size; 
    }
    else{
      return ((PI - angle) + (ag +PI)) <= size ; 
    }
    
  }
  
  // Only creates new ship after set time inteval of previously being spotted
  boolean canCreateNewShip(){
    if(lastShip == 0){
      lastShip = millis();
      return true; 
    }
    if(millis() - lastShip > shipGenerationRate){
      lastShip = 0;  
      return false; 
    }
    return false;
  }
}
