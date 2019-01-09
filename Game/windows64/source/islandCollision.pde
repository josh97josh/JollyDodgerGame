class islandCollision{
  circle c1; 
  ArrayList<island> islands;
  PVector collided,vertice,edge; 
 
  
  
  islandCollision(circle c1, ArrayList<island> islands){
    this.c1 = c1; 
    this.islands = islands;
  }
  
  
  ray[] getRays(){
    ray[] rays; 
     ray r1 = new ray(islands,new ArrayList<enemy>(),null);
      PVector norm = new PVector(-c1.vel.y,c1.vel.x);
      norm.normalize().mult(c1.size/2);
      
      PVector pos1 = PVector.add(c1.pos,norm);
      norm.mult(-1);
      PVector pos2 = PVector.add(c1.pos,norm);
      
      ray r2 = new ray(islands,new ArrayList<enemy>(),null);
      ray r3 = new ray(islands,new ArrayList<enemy>(),null);
  
      r1.findRays(c1.vel,0,c1.pos);
      r2.findRays(c1.vel,0,pos1);
      r3.findRays(c1.vel,0,pos2);
      rays = new ray[]{r1,r2,r3}; 
    
    return rays; 
  }
  
  // Check whether circle collides with island 
  void isColliding(){
    
    ray[] rays = getRays();
      ray r1 = rays[0] ; 
      ray r2 = rays[1];
      ray r3 = rays[2]; 
      
      float dis;
    
      if(r1.hitShape != null){
        if((collided = checkCollision(r1.hitShape)) != null){
           dis = getOverlap(collided);
           
          resolve(r1.centreShape,dis,r1.hitShape);
        }
      }
      
      else if(r2.hitShape != null){
        if((collided = checkCollision(r2.hitShape)) != null){
           dis = getOverlap(collided);
          resolve(r2.centreShape,dis,r2.hitShape);
        }
      }
      
      else if(r3.hitShape != null){
        if((collided = checkCollision(r3.hitShape)) != null){
           dis = getOverlap(collided);
          resolve(r3.centreShape,dis,r3.hitShape);
        }
      }
  }
  
  // Gets overlap of circle and island 
  float getOverlap(PVector collided){
    
    PVector toCollided = PVector.sub(collided,c1.pos) ;
    float angle = PVector.angleBetween(edge, toCollided); 
    return max(0,sin(angle) * toCollided.mag() - c1.size/2);
  }
  
  // Checks whether there is a collision between circle and edge
  PVector checkCollision(island is){
    for(int i= 0 ;i < is.numberVerteces; i ++){
      PVector vert = is.verteces.get(i);
      PVector ed = is.edges[i]; 
      PVector check = c1.getMinDistance(ed,vert);
      
      if(check == null){
        continue; 
      }
      float length = ed.mag();
      if(PVector.dist(vert,check) < length && PVector.dist(PVector.add(vert, ed), check ) < length){
        vertice = vert; 
        edge = ed;
        //System.out.println(check + " " + vert +" "  +  ed) ; 
        return check; 
      }
    }
    return null;
  }
  
  // Resolves the moeveaway velocity of the circle 
  void resolve(PVector middleShape,float overlap,island is){
    
    if( middleShape == null || middleShape.mag() == 0 || is.pos == null){
      return; 
    }
    pickUpTreasure(is);    
    
        if(PVector.add(c1.vel, edge).mag() > PVector.sub(c1.pos, edge ).mag()){
          edge.mult(-1);
        }
        float angleBet = PVector.angleBetween(edge,c1.vel);
        float angleToMove = atan2(edge.y,edge.x) - angleBet;
    
    // Put in range 
    if(angleToMove > PI){
      angleToMove -= TWO_PI;
    }
    else if(angleToMove < -PI){
      angleToMove += TWO_PI;
    }
    
    PVector direction = new PVector(cos(angleToMove),sin(angleToMove));
    direction.mult(c1.vel.mag() * c1.e);
    
    c1.vel = direction; 
    c1.pos.add( PVector.mult(direction,overlap));
    c1.rotation = atan2(c1.vel.y, c1.vel.x); 
    
  }
  
  // If circle is pirate then pick up the treasure 
  void pickUpTreasure(island is){
      if(c1.isPirate && c1.hasTreasure== 0 && is.hasTreasure!= 0 ){
      c1.hasTreasure = is.hasTreasure;
      is.removeTreasure();
      if(c1 instanceof player){
        treasurePickup.trigger(); 
      }
      else if(c1 instanceof enemy){
        enemyPickup.trigger();
      }
      
    }
  }
  
  
}
