import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Random; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {




Minim minim; 

// Declare sounds 
AudioPlayer startMusic,gameMusic, warning;
AudioSample cannon,treasurePickup,enemySteal,stolen,enemyPickup,splosh,killed,impact;

// declare images 
PImage lhimg, pirateImg, enemyImg, treasureImg, waterImg, treeImg;

// declare global variables 
boolean gamePaused;
int bossHappinessLevel,highScore,numBalls,level,playerTreasure,enemyTreasure,gameStage, oldHighScore; 
ArrayList<enemy> enemies; 
ArrayList<collision> collisions = new ArrayList<collision>();
ArrayList<islandCollision> islandCollisions = new ArrayList<islandCollision>();
islandCollision playerCollision; 
float objectSizes; 
ArrayList<PVector> treasureLocations;
ArrayList< projectile> cannons; 
boolean[] movements = new boolean[4]; 
PVector gameArea, gameSize, spawnLocation ;
playerInfo pinfo; 
lighthouse lh = new lighthouse();
Random rand = new Random();
player pl;
ArrayList<island> tr = new ArrayList<island> (); 
ArrayList<ray> newRay = new ArrayList<ray>();
BinaryTree bn;
setupGame sg = new setupGame();
playGame pg = new playGame(); 
background bg ;


// Game initialisation 
public void setup() {
  
  initialiseVariables();
  pinfo = new playerInfo();
  sg.getImages(); 
  minim=new Minim(this) ;
  sg.getSounds(); 
  
  startMusic.loop();
}
   
public void initialiseVariables(){
    gamePaused = false; 
    bossHappinessLevel = 100; 
    highScore = 0; 
    oldHighScore = 0;
    level = 0; 
    playerTreasure = 0; 
    enemyTreasure = 0;
    gameStage = 0;
    gameArea = new PVector(0,0); 
    gameSize = new PVector(966,568);
   bg = new background();
} 

public void draw() {
  background( 100,149,237);
  bg.drawBackground();
   //waterImg.resize((int) gameSize.x,(int)gameSize.y + 100); 
   //image(waterImg,gameArea.x,gameArea.y);
  
  if(gameStage == 0) {
    sg.drawStartScreen(); 
  }
  else if(gameStage == 2){
    sg.drawLevelCompletedScreen();
  }
  else if(gameStage == 3 || gameStage == 4){
    sg.drawDeathScreen();
    
  }else {
    
    lh.create(newRay,enemies,tr);
    sg.drawPlayScene();
    fill(0,0,0);
    
    pl.create();
    collisions.clear();
  
  pg.drawEnemies();
  
   for(terrain ter : tr){
    ter.drawShape();
  }
   pg.resolveCollisions();
   
    bn.create();
    bossHappinessLevel = playerTreasure - enemyTreasure + 100;  
    bossHappinessLevel = min(bossHappinessLevel, 100); 
    bossHappinessLevel = max(bossHappinessLevel, 0); 
    
    pinfo.create();
    
    pg.checkEndLevel();
    
  pg.drawAimer();
  pg.performMovements();
  if(playerTreasure > highScore){
      highScore = playerTreasure;
    }

  }
}

// Cannon fire 
public void mousePressed(){
  if(gameStage == 1 ){
    if(pl.playerCanFire()){
    cannons.add(new projectile(new PVector(pl.pos.x,pl.pos.y),new PVector(mouseX-pl.pos.x,mouseY-pl.pos.y),5,pl));
    }
  }
}

// Movement and game navigation 
public void keyPressed(){
  if(gameStage == 1){
 if(keyCode== LEFT || keyCode == 65){
   movements[3] = true; 
 }
 if(keyCode== RIGHT || keyCode == 68){
   movements[2] = true; 
   
 }if(keyCode== UP || keyCode == 87){
      movements[1] = true; 

 }if(keyCode== DOWN || keyCode == 83){
      movements[0] = true; 
 }
  }
 if(keyCode == 78){
   sg.newGame();
 }
 if(keyCode == 88){
   sg.newLevel();
 }
 
 if((gameStage == 3 || gameStage == 4 )&& keyCode == 32){
   gameStage = 0;
   startMusic.loop();
   gameMusic.pause(); 
   gameMusic.rewind();
 }
 if(gameStage == 2 && keyCode == 32){
   sg.newLevel();
   gameStage = 1; 
 }
 
 if(keyCode == 80){
   gamePaused = !gamePaused;
   
 }
 
}


public void keyReleased(){
   if(gameStage == 1){
 if(keyCode== LEFT || keyCode == 65){
   movements[3] = false; 
 }
 if(keyCode== RIGHT || keyCode == 68){
   movements[2] = false; 
   
 }if(keyCode== UP || keyCode == 87){
      movements[1] = false; 

 }if(keyCode== DOWN || keyCode == 83){
      movements[0] = false; 
 }
  }
}









     
class BinaryTree{
  PVector pos,size; 
  BinaryTree childLeft,childRight;
  float splitMin,objectSize;
  boolean specialSquare = false; 
  ArrayList<PVector> treasureLocations; 
  
  // Creates random areas for where islands can be drawn 
  BinaryTree(PVector pos, PVector size, float splitMin, ArrayList<island> is,ArrayList<PVector> treasureLocations, float objectSize){
    this.pos = pos; 
    this.size = size; 
    this.splitMin = splitMin; 
    this.objectSize = objectSize;
    this.treasureLocations = treasureLocations;
    generateChildren(is);
  }
  
  // Performs split algorithm 
  public void generateChildren(ArrayList<island> is){
    Random rand = new Random();
        int  n = rand.nextInt(2) ;
        if (n ==1 && size.x > splitMin) {
            HorizontalSplit(is);
        }
        else if(size.y > splitMin) {
            verticalSplit(is);
        }
        else{
          if(containsPoint(new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2))){
              //specialSquare = true; 
              return;
          }
          if(containsPoint(new PVector(gameArea.x + 5,gameArea.y +5)) || containsPoint(new PVector(gameArea.x + gameSize.x-5,gameArea.y +5)) || containsPoint(new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y-5) )|| containsPoint(new PVector(gameArea.x +5,gameArea.y+gameSize.y-5))){
            //specialSquare = true;
            return;
          }
          if(containsPoint(new PVector(gameSize.x/2 + gameArea.x + 5, gameArea.y + 5))){
            return;
          }
          n = rand.nextInt(2);
          if(n==1){
           
              island toAdd = new island(new PVector(pos.x , pos.y ),new PVector(size.x, size.y),6,treasureLocations);
            
            tr.add(toAdd );
            toAdd.putInTreasure();
            if(toAdd.hasTreasure != 0){
              treasureLocations.add(toAdd.treasurePos);
            }
          }
        }
    
  }
  
  // Used to prevent island being drawn on lighthouse 
  public boolean containsPoint(PVector position){
    if(pos.x < position.x  && pos.x + size.x > position.x){
            if(pos.y < position.y && pos.y + size.y > position.y){
              return true;
            }
          }
          return false;
  }
  
  // Split area vertically 
  public void verticalSplit(ArrayList<island> is){

        float n = random( splitMin/2,size.y-splitMin/2);
        
        childLeft = new BinaryTree(pos,new PVector (size.x,n),splitMin,is,treasureLocations,objectSize);
        childRight = new BinaryTree(new PVector (pos.x,pos.y + n), new PVector(size.x,size.y - n),splitMin,is,treasureLocations,objectSize);

    }

    // Split area horizontally 
    public void HorizontalSplit(ArrayList<island> is){

        float n = random( splitMin/2,size.x-splitMin/2);

        childLeft = new BinaryTree(pos,new PVector(n,size.y),splitMin,is,treasureLocations,objectSize);
        childRight = new BinaryTree(new PVector(pos.x + n,pos.y), new PVector(size.x -n,size.y),splitMin,is,treasureLocations,objectSize);
    }
    
    // Draw areas created by binary tree split 
    public void create(){
      if(gamePaused){
        noFill();
        if( specialSquare) {
          stroke(255,255,0); 
        }
        rect(pos.x,pos.y,size.x,size.y);
        if(childLeft != null){
          childLeft.create();
          childRight.create();
        }
        fill(0,0,0);
        stroke(0,0,0);
      }
      
    }
  
  
  
  
}
class background{
  float offsetRight = 0; 
  float offsetLeft = 0 ; 
  ArrayList<PVector> wave = new ArrayList();
  
  background(){
    for(int i = 0; i < gameArea.x + gameSize.x; i++ ){
        wave.add(new PVector(i,sin(i))); 
    }
  }
  
  public void drawBackground(){
    
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
    
    offsetRight = offsetRight + 0.1f; 
    offsetLeft = offsetLeft - 0.1f; 
  }
  
  
  public void makeWave(float offSetY){
    noFill();
    beginShape();
      for(PVector p: wave){
        
        curveVertex(gameArea.x + p.x * 10.0f,offSetY+((10*p.y)));
      }
     
    endShape();
    fill(0,0,0);
  }
  
  public void updateWave(float offset){
    wave.clear();
      for(int i = 0; i <= gameArea.x + gameSize.x + 1; i++ ){
        wave.add(new PVector(i,pow(2,sin(i + offset)) )); 
    }
  }
  
  
}
class circle{
  PVector pos,vel; 
  float  mass,size,e,dampener, rotation;
  boolean leftRound = true;
  boolean isPirate; 
  int hasTreasure,health,maxHealth,minDamage,maxDamage; 
  
  
  circle(PVector pos,PVector vel,float size){
    this.pos = pos;
    this.vel = vel;
    this.size = size;
    this.mass = size;
    this.e = 0.7f;
    this.dampener = 0.97f;
    this.hasTreasure = 0; 
  }
  
  
  public void create(){
    fill(0,0,0);
    ellipse(pos.x,pos.y,size,size);
    getNewPos();
  }
  
  // Applies velocity update on moving circle 
  public boolean getNewPos(){
    pos.x = pos.x + vel.x; 
    pos.y = pos.y + vel.y;
    applyDampener();
    return wallCollision();
  }
  
  // Applies a collision with a wall to the circle 
  public boolean wallCollision(){
    boolean needUpdate = false; 
    if(pos.x > gameArea.x + gameSize.x  -  size/2 ) {
      vel.x = - e*vel.x;
      pos.x = gameArea.x + gameSize.x - size/2;
      leftRound = !leftRound;
      needUpdate = true; 
    }
    else if(pos.x < gameArea.x + size/2){
      vel.x = -e*vel.x;
      pos.x = gameArea.x + size/2;
      leftRound = !leftRound;
      needUpdate = true; 
    }
    
    if(pos.y > gameArea.y + gameSize.y - size/2) {
      vel.y = -e*vel.y;
      pos.y =  gameArea.y + gameSize.y - size/2;
            leftRound = !leftRound;
      needUpdate = true; 

    }
    else if (pos.y < gameArea.y + size/2){
      vel.y = -e*vel.y;
      pos.y = gameArea.y + size/2;
            leftRound = !leftRound;
            needUpdate = true; 
    }
    return needUpdate; 
  }
  
  public PVector getMomentum(){
    return PVector.mult(vel,mass);
  }
  
  // Applys water resistance to circle 
  public void applyDampener(){
    vel.mult(dampener);
  }
  
  // Calculates the point of intersection of a line segment and the circle 
  public PVector getMinDistance(PVector d,PVector p){
     float r = size/2;
     float x1 = pos.x; 
     float y1 = pos.y;
     PVector min,max;
    
   if(abs(d.x) < abs(d.y)  ){
     float m = d.x/d.y;
     float c = p.x-m*p.y;
     float ax = 1 + m * m; 
     float bx = -2*y1 + 2 * m * c - 2 * m * x1;
     float cx = y1 * y1 + c*c - 2 * x1 * c + x1 * x1 - r * r;
     float check = bx*bx - 4*ax*cx;
     if(check < 0.0f){
       return null;
     }
     
     float miny = (-bx-sqrt(check))/(2*ax);
     float maxy = (-bx+sqrt(check))/(2*ax);
     
     min = new PVector(m*miny + c,miny);
     max = new PVector(m*maxy + c,maxy); 
     
   }
   else{
     float m = d.y/d.x;
     float c = p.y - m*p.x;
     
     float ax = 1 + m * m;
     float bx = -2*x1+2*m*c -2*m*y1;
     float cx = x1*x1 + c*c - 2*y1*c+y1*y1-r*r;
     
     float check = bx*bx - 4*ax*cx;
     if(check < 0.0f){
       return null;
     }
     
     float minx = (-bx-sqrt(check))/(2*ax);
     float maxx = (-bx+sqrt(check))/(2*ax);
     
     min = new PVector(minx,m*minx + c);
     max = new PVector(maxx,m*maxx + c);
   }
   
   if(PVector.dist(min,p)< PVector.dist(max,p) && (min.x-p.x)/d.x > 0 ){
       return min;
   }
   else if ((max.x - p.x)/d.x > 0){
     return max;
   }
   else return null; 
 }
 
 // Works out whether treasure can be dropped off by a pirate in a drop off zone
 public boolean canDropOff(){
   if(PVector.dist(pos,new PVector(gameArea.x,gameArea.y))< 100 + size/2 && hasTreasure == 4){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x + gameSize.x,gameArea.y))< 100 + size/2 && hasTreasure == 1){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x + gameSize.x,gameArea.y + gameSize.y))< 100 + size/2 && hasTreasure == 2){
     return true;
   }
   if(PVector.dist(pos,new PVector(gameArea.x,gameArea.y + gameSize.y))<100 + size/2 && hasTreasure == 3){
     return true;
   }
   
   return false;
   
 }
  
  
}
class collision{
  float e, ratio,overlap;
  circle c1,c2;
  PVector normal,dist,impulse,momentum1,momentum2;
  

  collision(circle c1, circle c2){
    this.c1 = c1; 
    this.c2 = c2;
    this.e=min(c1.e,c2.e);    
    
  }
  
  // Detects whether a circle has come into contact with another circle
  public boolean isColliding(){
    this.dist = PVector.sub(c1.pos, c2.pos) ;    

    //System.out.println(dist.mag());
    if(dist.mag() < c1.size/2 + c2.size/2){
      
      moveApart();
      normal = dist.normalize();

      resolveImpulse();
      boolean place =  c1.leftRound;
      c1.leftRound = c2.leftRound;
      c2.leftRound = place;
      return true;
            
    }
    return false;
  }
  
  // Calculates the impulse of a collision
  public void calculateImpulse(){
    float j  = PVector.sub(c2.vel,c1.vel).dot(normal);
    float inversemass = 1/c1.mass + 1/c2.mass; 
    j = j * -(1+e);
    j = j/inversemass;
    impulse = PVector.mult(normal, j);
  }
  
  // resolves the new velocities after collision
  public void resolveImpulse(){
    
    calculateImpulse();
    
    c1.vel.sub(PVector.mult(impulse, 1/c1.mass));
    c2.vel.add(PVector.mult(impulse, 1/c2.mass));
  }
  
  // Moves circles apart to prevent overlap 
  public void moveApart(){
    if(c1.hasTreasure!= 0 && c2.hasTreasure == 0 && c2.isPirate){
      c2.hasTreasure = c1.hasTreasure; 
      c1.hasTreasure = 0; 
    }
    else if(c1.hasTreasure ==0 && c2.hasTreasure!= 00 && c1.isPirate){
      c1.hasTreasure = c2.hasTreasure; 
      c2.hasTreasure = 0; 
    }
    float overlap = c1.size/2 + c2.size/2 - dist.mag(); 
    normal = dist.normalize();
    PVector move = PVector.mult(normal, overlap/2);
    c1.pos.add(move); 
    c2.pos.sub(move);
    
    
  }
  
}
class enemy extends circle{
  boolean willWander = true;
  PVector approaching = new PVector(0,0);
  island avoidingShape;
  ArrayList<PVector> treasureLocations;
  float lastFired, shotRate; 
  PVector[] possibleContacts = new PVector[3];
  
  enemy(PVector pos,PVector vel,float size){
   super(pos,vel,size);
   lastFired = 0; 
   Random rand = new Random();
   int  n = rand.nextInt(2) ;
   if(n==1){
     isPirate = true;
   }

   generateStats();
 }
  
  // Returns whether an enemy can fire again 
  public boolean canFire(){
    if(gamePaused){
      return false;  
    }
    if(lastFired == 0){
      lastFired = millis();
      return true; 
    }
    if(millis() - lastFired > shotRate){
      lastFired = 0;  
      return false; 
    }
    return false;
  }
  
 // calculates what type of movement the enemy will perform 
 public void enemyMovement(player pl, ArrayList<island> islands, ArrayList<PVector> treasureLocations){
   if(gamePaused){
     return; 
   }
   this.treasureLocations = treasureLocations; 
   if (isPirate && hasTreasure == 0){
     if (treasureLocations.size() == 0){
       seek(pl.pos,islands);
       return;
     }
     seek(getClosest(),islands);
     return;
     
   }
   else if(isPirate && hasTreasure != 0){
     seek(getDropOffPoint(),islands);
     return ;
   }
   else if(!isPirate && pl.detected){
     if(PVector.dist(pl.pos,pos)> 100){
       seek(pl.pos,islands);
     }

   }
   else{
     if(PVector.dist(pos, pl.pos) < 200 && PVector.dist(pl.pos,pos)> 100 ){
       seek(pl.pos,islands); 
     }
     else{
       wander();
     }
     return; 
   }
   
 }
 
 // Get direction of drop off point if has treasure
 public PVector getDropOffPoint(){
   if(hasTreasure == 4){
     return new PVector(gameArea.x+5,gameArea.y+5);
   }
   if(hasTreasure == 1){
     return new PVector(gameArea.x + gameSize.x-5,gameArea.y + 5);
   }
   if(hasTreasure == 2){
     return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y-5);
     
   }
   if(hasTreasure == 3){
     return new PVector(gameArea.x + 5,gameArea.y + gameSize.y-5);
   }
   return null;
 }
 
 // Gets closest treasure to current position 
 public PVector getClosest(){
   PVector closest = null; 
   float dist = 10000;
   for(PVector p: treasureLocations){
     float toTreasure = PVector.dist(pos,p);
     if(toTreasure < dist){
       closest = new PVector(p.x,p.y);
       dist = toTreasure;
     }
   }
   return closest;
 }
 
 // Move towards location using collision avoidance
 public void seek(PVector location, ArrayList<island> islands){
   if(location == null){
     return; 
   }
   
   PVector dir = PVector.sub(location ,pos);
   PVector[] positions = getPositions(dir);
   ray[] rays = getRays(dir, positions,islands);
   int bestRay = getClosestCollision(dir, positions); 
   
   if(bestRay == -1 ) { 
     PVector towards = new PVector(dir.x, dir.y).normalize(); 
     approach(towards) ; 
     return ; 
   }
   else if(PVector.dist(rays[bestRay].hitShape.verteces.get(0),gameArea) == 0  ){
     PVector towards = new PVector(dir.x, dir.y).normalize(); 
     approach(towards) ; 
     return ; 
   }
   
   PVector toCorner = getCornerToAvoid(rays[bestRay].hitShape, dir,positions[bestRay]); 
   getPositionToMoveTo(toCorner, rays[bestRay].hitShape,positions[bestRay]); 
   
   
 }
 
 // Get expected collisions based on current velocity 
 public PVector[] getPositions(PVector dir){
   PVector norm = new PVector(-dir.y,dir.x).normalize().mult(size/2);
   PVector pos1 = PVector.add(pos,norm);
   norm.mult(-1); 
   PVector pos2 = PVector.add(pos,norm);
   return new PVector[]{pos,pos1,pos2};
   
 }
 
 // Get rays based on tangents and current position 
 public ray[] getRays(PVector dir, PVector[] positions, ArrayList<island> islands){
   ray[] rays = new ray[3];
   for(int i = 0; i< 3; i++){
      rays[i] = new ray(islands, new ArrayList<enemy>(),null); 
      possibleContacts[i] = rays[i].findRays(dir,0,positions[i]); 
   }
  return rays;    
 }
 
 // return closest collision
 public int getClosestCollision(PVector dir, PVector[] positions){
   int bestRay = -1; 
   float closest = dir.mag() -  15 ;
   for(int i = 0; i< 3; i++){
      float toCheck = PVector.sub(positions[i], possibleContacts[i]).mag();
     if(toCheck < closest){
       closest = toCheck; 
        bestRay = i; 
     }
   }
   return bestRay; 
 }
 
 // Get corner needed to avoid 
 public PVector getCornerToAvoid(island is, PVector dir, PVector pos1){
   PVector posToAvoid1 = new PVector(0,0) ; 
   PVector maxValue = new PVector(0,0);
   float biggestAngle1 = 0; 
   
   for(PVector v: is.verteces){
     PVector toPoint = PVector.sub(v,pos1); 
     float angleToCheck = PVector.angleBetween(dir,toPoint); 
     if(angleToCheck > biggestAngle1){
       biggestAngle1 = angleToCheck; 
       maxValue = new PVector(toPoint.x,toPoint.y) ; 
       posToAvoid1 = new PVector(v.x,v.y); 
     }
   }
   
   PVector posToAvoid2 = new PVector (0,0) ; 
   float biggestAngle2 = 0; 
  
   for(PVector v: is.verteces){
     PVector toPoint = PVector.sub(v,pos1); 
     float angleToCheck = PVector.angleBetween(maxValue, toPoint) ; 
     if(angleToCheck > biggestAngle2){
       biggestAngle2 = angleToCheck; 
       posToAvoid2 = new PVector(v.x,v.y); 
     }
   }
   
   if( abs(biggestAngle2 - 2*biggestAngle1) < 5) {
     
     PVector toLeft = PVector.sub(posToAvoid1, pos1);
     PVector toRight = PVector.sub(posToAvoid2, pos1); 
     if(PVector.angleBetween(toLeft, vel) < PVector.angleBetween(toRight, vel) ){
       return posToAvoid1; 
     }
     else{
       return posToAvoid2; 
     }
   }
   else{
     return posToAvoid2; 
   }
 }
 
 // Get position required to move towards inorder to avoid shape 
 public void getPositionToMoveTo(PVector corner , island is, PVector pos1 ){
      fill(0,0,0); 
   PVector direction = PVector.sub(corner,pos1); 
   PVector norm = new PVector(-direction.y, direction.x); 
   norm.normalize().mult(size); 
   
   PVector option1 = PVector.add(norm, direction);
   PVector option2 = PVector.sub(direction, norm ) ; 
    
   //is.lightUp = true; 
   //ellipse(option1.x + pos1.x, option1.y + pos1.y,10,10); 
   //   ellipse(option2.x + pos1.x, option2.y + pos1.y,10,10); 

   PVector toTest1 = PVector.add(option1, pos1); 
   PVector toTest2 = PVector.add(option2,pos1); 
   
   if(PVector.dist(toTest1,is.averageVertex) > PVector.dist(toTest2, is.averageVertex)){
     approach(option1); 
     approaching = option1;
   }
   else{
     approach(option2); 
          approaching = option2; 

   }
 }
 
 // Random movement 
 public void wander(){
   float angle = atan2(vel.y,vel.x);
   angle += random(-PI/36, PI/36);   
   vel = new PVector(cos(angle), sin(angle)).mult(1);
 }
 
 // Move velocity towards position to prevent zigzagging movement 
 public void approach(PVector towards){
  
   //System.out.println(towards); 
   //line(pos.x, pos.y, pos.x + towards.x, pos.y + towards.y) ; 
   towards.normalize(); 
   towards.mult(2);
   float diffy = vel.y - towards.y;
   float diffx = vel.x - towards.x;
   
     diffy *= 0.2f;
     diffx *= 0.2f;
   
   
   vel.y = vel.y - diffy;
   vel.x = vel.x - diffx;
 }
 
 
 // Draw and update enemy position 
 public void create(){
   noStroke();

    fill(0,0,0);
    
    fill(255,255,255);
    if(gamePaused) {
      if(isPirate){
        strokeWeight(5);  
        stroke(0,0,0);
      }
    }
    
    if(hasTreasure!= 0){
      strokeWeight(5);
            stroke(255,215,0);
    }
      fill(100,149,237);
    ellipse(pos.x,pos.y,size,size);
    PImage ei = enemyImg.copy();
    ei.resize((int)size,(int)size);
    image(ei,pos.x - size/2 ,pos.y-size/2);
      fill(0,0,0);
      textAlign(CENTER,CENTER);
      textSize(12);
      text(health,pos.x, pos.y + size/2 + 5);
      textSize(30);
      textAlign(LEFT);
      
    if(canDropOff()){
      enemySteal.trigger();
      enemyTreasure += random(50,100);
      hasTreasure = 0;
    }
    if(!gamePaused){
      getNewPos();
    }
        fill(0,0,0);
        stroke(0,0,0);
        strokeWeight(1);
  }
  
  // Get enemt stats based on current player level 
  public void generateStats(){
    
    maxHealth = (int)(10 + random(level*20,level* 30));
    health = maxHealth;
   maxDamage = (int)(10 + random(level*5, level*10));
   minDamage = (int)random(maxDamage/2,maxDamage);
   shotRate = random(max(20,2000-level*5),2000);
    
  }
  
}
class island extends terrain{
  PVector pos,size; 
  int hasTreasure = 0;
  PVector treasurePos = new PVector(0,0); 
  ArrayList<PVector> treasureLocations;
  int originalVerteces;
  boolean hasTree; 
  
  
  
  island(PVector pos, PVector size, int numberVerteces, ArrayList<PVector> treasureLocations){
    
    super();
    this.numberVerteces = numberVerteces;
    this.originalVerteces = numberVerteces; 
    this.treasureLocations = treasureLocations;
    doesShrink(pos,size);
    this.pos = new PVector(pos.x + 2*objectSizes, pos.y + 2*objectSizes); 
    this.size = new PVector(size.x - 4* objectSizes, size.y - 4* objectSizes);
    this.hasTree = true; 
    addVerteces(); 
  }
  
  // create space between island and edge of game area 
  public void doesShrink(PVector pos , PVector size){
    if(pos.x == gameArea.x){
      pos.x += objectSizes;  
      size.x -= objectSizes; 
    }
    if(pos.x + size.x == gameArea.x + gameSize.x ){
      size.x -= objectSizes; 
    }
    if(pos.y == gameArea.y){
      pos.y += objectSizes ;
      size.y -= objectSizes;
    }
    if(pos.y + size.y == gameArea.y + gameSize.y){
      size.y -= objectSizes;
    }
  }
  
  
  island(ArrayList<PVector> verteces){
    this.verteces = verteces; 
    this.numberVerteces = verteces.size();
    this.edges = new PVector[numberVerteces];
    getEdges();
    toColor = false;
  }
  
  // Get verteces within given area 
  public void generateVerteces(){
    for( int i = 0; i< numberVerteces ; i++){
        verteces.add(new PVector(random(pos.x,pos.x + size.x),random( pos.y, pos.y + size.y)));      
    }
    getAverageVertex();    
    orderVerteces(0, verteces.size()-1);
  }
  
  public void addVerteces(){
    generateVerteces();
    getEdges();
    
  }
  
  public float toDegrees(float angle){
      return angle/TWO_PI * 360;
  }
  
  // Get edges between verteces 
  public void getEdges(){
    edges = new PVector [numberVerteces];
    for(int i = 0; i< numberVerteces - 1 ;i++){
        edges[i] =  PVector.sub(verteces.get(i+1),verteces.get(i));
      }
      edges[numberVerteces-1] = PVector.sub(verteces.get(0),verteces.get(verteces.size() -1));
}

 // Add treasure onto random vertex
public void putInTreasure(){
  Random rand = new Random();
  int willPutin = rand.nextInt(2);
  if(willPutin == 1){
  int  n = rand.nextInt(verteces.size()) ;
  int j = rand.nextInt(5);
    hasTreasure = j; 
    hasTree = false;
    PVector v = verteces.get(n);
    treasurePos = new PVector(v.x,v.y);
  }
    
}

// Remove treasure in collision 
public void removeTreasure(){
  hasTreasure = 0; 
    for(PVector t: treasureLocations){
      if(t.x == treasurePos.x && t.y == treasurePos.y) {
        treasureLocations.remove(t);
        break;
      }
    }

  treasurePos = null; 
}

public void drawShape(){
       super.drawShape();
  if(hasTreasure!= 0){
    noStroke();
    treasureImg.resize(40,30);
    image(treasureImg,treasurePos.x -10,treasurePos.y-10);
  }
  else if(hasTree ){
    treeImg.resize((int) 50,(int)50); 
   image(treeImg,averageVertex.x -25 ,averageVertex.y -50 );
  }
  
  
  
  fill(0,0,0);
  stroke(0,0,0);
}




  
}
class islandCollision{
  circle c1; 
  ArrayList<island> islands;
  PVector collided,vertice,edge; 
 
  
  
  islandCollision(circle c1, ArrayList<island> islands){
    this.c1 = c1; 
    this.islands = islands;
  }
  
  
  public ray[] getRays(){
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
  public void isColliding(){
    
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
  public float getOverlap(PVector collided){
    
    PVector toCollided = PVector.sub(collided,c1.pos) ;
    float angle = PVector.angleBetween(edge, toCollided); 
    return max(0,sin(angle) * toCollided.mag() - c1.size/2);
  }
  
  // Checks whether there is a collision between circle and edge
  public PVector checkCollision(island is){
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
  public void resolve(PVector middleShape,float overlap,island is){
    
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
  public void pickUpTreasure(island is){
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
    public void create(ArrayList<ray> newRay, ArrayList< enemy> circles, ArrayList< island> tr ){
     if(!gamePaused){
      angle = angle + 0.01f ; 
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
  public ray addEdges(){
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
  public void detectPlayer(){
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
  
  public boolean isContained(float ag){
    if(ag > angle){
      return (ag - angle) <= size; 
    }
    else{
      return ((PI - angle) + (ag +PI)) <= size ; 
    }
    
  }
  
  // Only creates new ship after set time inteval of previously being spotted
  public boolean canCreateNewShip(){
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
class playGame{
public void performMovements(){
   if(movements[0]){
     pl.movey(-1);
   }
   if(movements[1]){
     pl.movey(1);
   }
   if(movements[2]){
     pl.movex(1); 
   }
   if(movements[3]){
     pl.movex(-1); 
   }
}

// Aimer used for player cannon firing 
public void drawAimer(){
  noFill();
  line(mouseX,mouseY-15, mouseX,mouseY+15);
    line(mouseX-15,mouseY, mouseX+15,mouseY);
    ellipse(mouseX,mouseY,10,10);
    fill(0,0,0);

}

// Manages cannon fire and destroys cannon if travels off the screen or hits a player
public void cannonCollisions(){
  for(int i = 0; i< cannons.size() && i>=0; i++){
    projectile c = cannons.get(i);
    c.create();
    if(!c.offScreen()){
      
      for(int j = 0; j < enemies.size();j++){
        enemy e = enemies.get(j);
        if(e.pos.x != c.firedBy.pos.x && e.pos.y != c.firedBy.pos.y){
        if(new collision(e,c).isColliding()){
          cannons.remove(c);
          c.dealDamage(enemies.get(j));
          i--;
        }
        }
      }
      
      if(new collision(pl,c).isColliding()){
        if(pl.pos.x != c.firedBy.pos.x && pl.pos.y != c.firedBy.pos.y){
          cannons.remove(c);
          c.dealDamage(pl);
          i--;
        }
      }
      
    }
  }
}


public void drawEnemies(){
  for(int i = 0; i< enemies.size() && i >= 0;i++){
    enemy e = enemies.get(i);
    if(e.health<= 0){
      enemies.remove(i);
      i--;
    }
    if(PVector.dist(e.pos, pl.pos) < 200)  {
      if(e.canFire()){
        
        cannons.add(new projectile(new PVector(e.pos.x,e.pos.y),new PVector(pl.pos.x- e.pos.x,pl.pos.y- e.pos.y),5,e));
      }
      else{
        //System.out.println("idnt fire") ;
      }
    }
    
    e.create();
    e.enemyMovement(pl,tr,treasureLocations);
  }
  
  for (int i = 0; i< enemies.size();i++){
    collisions.add(new collision(pl,enemies.get(i))); 

    for(int j = 0; j< i; j++){
      collisions.add(new collision(enemies.get(i),enemies.get(j))); 
    }
  }
  islandCollisions.clear();
  for(enemy e: enemies){
    islandCollisions.add(new islandCollision(e,tr));
  }
}

// Resolves collisions between all game objects 
public void resolveCollisions(){
 pg.cannonCollisions();
  
   for(int i = 0; i < islandCollisions.size(); i++){
    islandCollisions.get(i).isColliding();
  }
  playerCollision.isColliding();
  
  for(int i = 0; i < collisions.size(); i++){
    collisions.get(i).isColliding();
  } 
}

// Checks whether level has been completed or if player has died
public void checkEndLevel(){
  if(treasureLocations.size() == 0 && pl.hasTreasure == 0){
      if(enemyHasAnyTreasure() ==0){
        gameStage = 2; 
      }
    }
    
    if(pl.health <= 0){
      killed.trigger(); 
      gameStage = 4;
    }
  }
  
  public int enemyHasAnyTreasure(){
  int count= 0; 
  for(enemy e: enemies){
    if(e.hasTreasure != 0){
      count ++;
    }
  }
  return count; 
}

  
}
class player extends circle{
  boolean detected;
  float lastFired,shotRate,maxSpeed; 
  
 player(PVector pos,PVector vel,float size){
   super(pos,vel,size);
   isPirate = true;
   detected = false; 
    maxDamage = 30; 
   minDamage = 30; 
   shotRate = 1000; 
   health = 100;
   maxHealth = 100;
   maxSpeed = 3; 
   rotation = 0; 
 }
  
  // Player Movement 
  public void movex(int x){
   if(x < 0) {
     rotation -= PI/32;   
   }
   else{
     rotation += PI/32 ;  
   }
   
  }
  
  public void movey(int y){
    PVector direction = new PVector(cos(rotation), sin(rotation)).mult(0.2f); 
    if(y < 0) {
      vel.sub(direction);
    }
    else{
      vel.add(direction) ;   
    }
    if(vel.mag() > maxSpeed){
      vel.normalize().mult(maxSpeed) ;   
    }
    
    
  }
  
  // Checks whether player can fire after firing previous cannon 
  public boolean playerCanFire(){
    if(gamePaused){
      return false;  
    }
    if(lastFired == 0){
      lastFired = millis();
      return true; 
    }
    if(millis() - lastFired > shotRate){
      lastFired = 0;  
      return true; 
    }
    return false;
    
  }
  
   public void create(){
     //System.out.println(health);
         
    if(!gamePaused){
      if(getNewPos()){
        rotation = atan2(vel.y,vel.x); 
      }
    }
    fill(0,0,0); 
    stroke(0,0,0);
    strokeWeight(5);
    PVector dir = new PVector(cos(rotation), sin(rotation)).mult(16) ;
    line(pos.x,pos.y, pos.x + dir.x, pos.y + dir.y); 
    
    noStroke();
    strokeWeight(5);

    fill(0,0,0);
    if(hasTreasure == 1){
      stroke(255,0,0);
    }
    else if(hasTreasure == 2){
      stroke(255,165,0);
    }
    else if(hasTreasure == 3){
      stroke(255,105,180);
      
    }else if(hasTreasure == 4){
      stroke(0,255,0);
    }
      fill(100,149,237);
    ellipse(pos.x,pos.y,size,size);
    pirateImg.resize((int)size,(int)size); 
    image(pirateImg,pos.x-size/2,pos.y-size/2);
    
    if(canDropOff()){
      stolen.trigger();
      playerTreasure += random(75,125);
      hasTreasure = 0;
    }
   
    fill(0,0,0); 
    stroke(0,0,0);
    
    strokeWeight(1);
    
  }
  
 // Upgrades player at end of level 
 public void playerUpgrade(){
   maxHealth += random(10,50);
   health = maxHealth;
   
   int toAdd = (int)random(5,25);
   maxDamage += toAdd;
   minDamage += random(5, min(25, maxDamage-minDamage));
   shotRate = max(20, shotRate - random(5,10));
   //speed += 0.1;
 }
  
}
class playerInfo{ 

  public void create(){
    fill(0,0,0); 
    textSize(20); 
    textAlign(CENTER,CENTER);

    drawInfo(0); 
    fill(255,255,255);
    drawInfo(2); 
    
    fill(0,0,0);
  }
  
  // Used in drawing the whole HUD for the player
  public void drawInfo(float offset){
    text( "HighScore: ", gameArea.x + 170 + offset ,gameArea.y + 10); 
    text(highScore,gameArea.x + 170 + offset,gameArea.y + 30);
    
    text("Boss", gameArea.x + gameSize.x - 170 + offset,10);
    text("Happiness: ", gameArea.x + gameSize.x - 170 + offset,30);
    text(bossHappinessLevel,gameArea.x + gameSize.x - 170 + offset,50);
    
        
    text("Enemies: " + numBalls  ,gameArea.x + gameSize.x - 170 + offset,gameArea.y + gameSize.y -20);
    text("Treasure:" + (treasureLocations.size() + pg.enemyHasAnyTreasure()),gameArea.x + gameSize.x - 170 + offset,gameArea.y + gameSize.y -40);
    
    PVector position = new PVector(gameArea.x + 170 + offset, gameArea.y + gameSize.y -20 );
    
    text("Health: " + pl.health, position.x, position.y -60); 
    //text("Max Health: " + pl.maxHealth,position.x, position.y +30);
    text("Fire Rate: " + (int)pl.shotRate,position.x,position.y -40);
    text("Max Dam: " + pl.maxDamage,position.x, position.y -20);
    text("Min Dam: " + pl.minDamage,position.x, position.y);
    
    
    PVector posTitle = new PVector(gameSize.x/2 + gameArea.x + offset, 10);
    text("Level: " + level,posTitle.x, posTitle.y);
    text("PlayerTreasure: " + playerTreasure , posTitle.x, posTitle.y + 20); 
    text("Enemy Treasure: " + enemyTreasure,posTitle.x, posTitle.y +40);
  }
  
}
class projectile extends circle{
  
  float minDamage,maxDamage; 
  
  circle firedBy;
  
  projectile(PVector pos,PVector vel,float size,circle firedBy){
    super(pos,vel,size);
    this.firedBy = firedBy;
    PVector toMove = new PVector(vel.x,vel.y);
    toMove.normalize().mult(size/2 + firedBy.size/2);
    this.pos = PVector.add(pos,toMove);
    
    dampener = 1; 
    
    vel = vel.normalize().mult(6);
    cannon.trigger();
    
    
  }
  
  // Single ray used for small circle, Checks whether ball has hit an circle 
  public boolean canCreate(){
    ray r = new ray(tr,enemies,pl);
     r.findRays(vel,0,pos);
     circle sh = r.hitCircle;
     if(sh!= null && firedBy != null){
       if(sh.pos != firedBy.pos){
         if(PVector.dist(pos,sh.pos) < size/2 + sh.size/2){
           dealDamage(sh);
           return false;
         }
       }
     }
     return true;
  }
  
  public void dealDamage(circle sh){
    Random rand = new Random();
    int toDeal  = rand.nextInt(firedBy.maxDamage+1 - firedBy.minDamage) + firedBy.minDamage ;
    //System.out.println(toDeal); 
    sh.health -= toDeal ;
    if(sh.health <=0 && firedBy.isPirate && sh.hasTreasure != 0 && firedBy.hasTreasure == 0){
      firedBy.hasTreasure = sh.hasTreasure;
    }
    impact.trigger(); 
    
  }
  
  // Gets movement for the projectile 
  public boolean getNewPos(){
    
    pos.x = pos.x + vel.x; 
    pos.y = pos.y + vel.y;
    applyDampener();
    return true; 
    
  }
  
  // Checks whether cannon ball has moved off the screen 
  public boolean offScreen(){
    if(pos.x< gameArea.x + size/2 || pos.x > gameArea.x + gameSize.x - size/2 || pos.y< gameArea.y + size/2 || pos.y > gameArea.y + gameSize.y  - size/2){
      return true; 
    }
    return false; 
  }
}
class ray{
  ArrayList<island> tr = new ArrayList<island> (); 
  ArrayList<enemy> circles = new ArrayList<enemy>();
  terrain shape;
  circle circ,hitCircle;
  player pl; 
  PVector pos = new PVector(gameArea.x + (gameSize.x)/2, gameArea.y + (gameSize.y)/2);
  PVector[] dir,minRay;
  PVector centreShape;
  island hitShape;
  ArrayList<PVector> vertecesToReturn = new ArrayList<PVector>();
  
  // Used for getting the rays from lightHouse for an island 
  ray(ArrayList<island> tr, terrain shape, player pl){
    this.tr = tr;
    this.shape = shape;
    this.pl = pl;
    this.dir = new PVector[shape.numberVerteces * 2];
    this.minRay = new PVector[shape.numberVerteces * 2];
    this.centreShape = new PVector();
  }
  
  // Used for getting the rays from lighthouse beam for a circle
  ray(ArrayList<island> tr, circle en, player pl){
    this.tr = tr;
    this.circ = en; 
    this.pl = pl;
    this.dir = new PVector[4]; 
    this.minRay = new PVector[4]; 
    this.centreShape = new PVector(); 
  }
  
  // Used for generic use of rays, e.g for enemy collision avoidance
  ray(ArrayList<island> tr , ArrayList<enemy> circles,player pl){
    this.tr  = tr; 
    this.circles = circles;
    this.pl = pl;
    this.minRay = new PVector[3];
    this.dir = new PVector[3];
    this.centreShape = new PVector();
  }
  
  // Get angles to shine rays for island for beam
   public ArrayList<PVector> getAnglesForShape(ArrayList<island> tr, ArrayList<enemy> circles){
     this.circles = circles; 
     this.tr = tr;
     vertecesToReturn.clear();
     if(shape == null){
       return vertecesToReturn;
     }
    for(int i = 0; i< shape.numberVerteces; i++){
      
      PVector toVertex = PVector.sub(shape.verteces.get(i),pos);
      toVertex.normalize();
      float angle = atan2(toVertex.y,toVertex.x);
      int j = i*2;
      toVertex = new PVector(cos(angle + 0.00001f),sin(angle+ 0.00001f));
      findRays(toVertex,j,pos);
     addVertex(j);
      j+=1;
      toVertex = new PVector(cos(angle - 0.00001f),sin(angle- 0.00001f));
      findRays(toVertex,j,pos);
     addVertex(j);
    }
    return vertecesToReturn;
  }
  
  // Get tangent angle for circle to shine rays for beam 
  public ArrayList<PVector> getAnglesForCircle(ArrayList<island> tr , ArrayList<enemy> circles){
    this.tr = tr; 
    this.circles = circles; 
    vertecesToReturn.clear();
    if(circ == null){
       return vertecesToReturn;
     }
     PVector toVertex = PVector.sub(circ.pos,pos);
     float angle = atan2(toVertex.y, toVertex.x);
     float dist = PVector.dist(pos,circ.pos);
     float adj = dist * dist - (circ.size/2) * (circ.size/2);
     float offset1 = dist * dist - ((circ.size/2) + 1) * ((circ.size/2) + 1);
     float offset2 = dist * dist - ((circ.size/2) - 1) * ((circ.size/2) - 1);
     
     if(adj > 0 && offset1 > 0 && offset2 > 0){
       adj = sqrt(adj);
       offset1 = sqrt(offset1);
       offset2 = sqrt(offset2);
     }
     else{
      return vertecesToReturn; 
     }
     
     float tangentUp = atan2((circ.size/2)+1,offset1);
     float tangentDown = atan2((circ.size/2)-1, offset2);
     
     float[] toTestArray = new float[]{angle + tangentUp, angle + tangentDown, angle - tangentUp, angle - tangentDown};
     
     for(int i = 0; i< 4; i++){
       float toTest = toTestArray[i]; 
       toVertex = new PVector(cos(toTest),sin(toTest));
       findRays(toVertex,i,pos);
       addVertex(i);
     }
     return vertecesToReturn;
  }
  
  public void addVertex(int i){
    if(minRay[i] != null){
      vertecesToReturn.add(minRay[i]);
    }
  }
  
  // Gets minimum distance and the point of closest contact for a ray shined from point 'pos' in the direction 'd' 
  public PVector findRays(PVector d, int i, PVector pos){
   this.pos = pos;
    dir[i] = d;
    minRay[i] = new PVector(10000,10000);
    islandCheck(i);
    minRay[i] = new PVector(pos.x + dir[i].x * minRay[i].x, pos.y + dir[i].y * minRay[i].x);
    
    circleCheck(d,i);
    playerCheck(d,i);
    
    if(gamePaused){
      drawRay(i);
    }
    return minRay[i];
  }
  
  // Checks for ray contact with island
  public void islandCheck(int i){
    for(island sh: tr){
      for(int j = 0; j< sh.numberVerteces; j++){
        PVector seg = sh.verteces.get(j);
        PVector linedr = sh.edges[j];
        boolean hasUpdated = updateMinRay(seg,linedr,i);
        if(hasUpdated){
          sh.getAverageVertex();
          centreShape = new PVector(sh.averageVertex.x,sh.averageVertex.y);
          hitShape = sh;
        }
      }
    }
    addScreenEdges(i);
  }
  
  // checks for ray contact with enemies
  public void circleCheck(PVector d, int i){
    for(enemy en: circles){
      PVector crossOver = en.getMinDistance(d,pos);
      //System.out.println(crossOver);
      if(crossOver != null){
        if(PVector.dist(crossOver,pos) < PVector.dist(minRay[i],pos)){
          minRay[i] = crossOver;
          hitCircle = en;
        }
      }
    }
  }
  
  // checks for ray contact with player
  public void playerCheck(PVector d, int i){
    if(pl!= null){
      PVector crossOver = pl.getMinDistance(d,pos);
        //System.out.println(crossOver);
        if(crossOver != null){
          if(PVector.dist(crossOver,pos) < PVector.dist(minRay[i],pos)){
            minRay[i] = crossOver;
            hitCircle = pl;
          }
        }
    }
  }
  
  
  // Update closest point for ray if it was closer than previously recorded value 
  public boolean updateMinRay(PVector seg, PVector linedr,int i){
      PVector calc = new PVector(0,0);
      float gradient = dir[i].y/dir[i].x;
      float gradientLine = linedr.y/linedr.x;
        if(abs(gradientLine) == abs(gradient) ){
          return false;
        }
      calc.y = ((dir[i].x * (seg.y - pos.y)) + (dir[i].y * (pos.x - seg.x)))/(linedr.x * dir[i].y - linedr.y * dir[i].x);
      calc.x = (seg.x + (linedr.x*calc.y) - pos.x)/dir[i].x;
      
      if(calc.x >= 0 && calc.y >= 0 && calc.y <= 1){
        if(minRay[i] == null){
          minRay[i] = calc; 
          return true;
        }
        else if(calc.x < minRay[i].x){
          minRay[i] = calc;
          return true;
        }
        else{
          return false;
        }
      }
      return false;
    }
    
    // Adds screen edges as a shape to be included in collision point detection 
    public void addScreenEdges(int i){
      updateMinRay(new PVector(gameArea.x,gameArea.y), new PVector(gameArea.x + gameSize.x,0),i); 
      updateMinRay(new PVector(gameArea.x + gameSize.x,gameArea.y), new PVector(0,gameArea.y + gameSize.y),i); 
      updateMinRay(new PVector(gameArea.x,gameArea.y + gameSize.y), new PVector(gameArea.x + gameSize.x,0),i); 
      updateMinRay(new PVector(gameArea.x,gameArea.y), new PVector(0,gameArea.y + gameSize.y),i);
    }
  
  // Draws the direction and collision point of an array 
  public void drawRay(int i){
    //System.out.println(minRay);
    fill(0,0,0);
    if(minRay[i] != null){
     line(pos.x,pos.y, minRay[i].x, minRay[i].y);
     //System.out.println(pos.x + dir.x *minRay.x + " " + );
     ellipse(minRay[i].x,minRay[i].y, 10,10);
    }
  }
}
class setupGame{
 
  // Initialise images
public void getImages(){
  lhimg = loadImage("lighthouse.png");
  pirateImg = loadImage("playerBoat.png");
  enemyImg = loadImage("enemyBoat.png");
  treasureImg = loadImage("treasure.png");
  waterImg = loadImage("water.jpg"); 
  treeImg = loadImage("tree.png");
}
  
  // Initialise sounds
  public void getSounds(){
    startMusic = minim.loadFile("startMusic.wav")  ;  
    gameMusic = minim.loadFile("gameMusic.wav"); 
    warning = minim.loadFile("warning.wav");
    cannon  = minim.loadSample("cannon.wav"); 
    treasurePickup = minim.loadSample("treasureP.wav");
    enemySteal = minim.loadSample("enemySteal.wav"); 
    stolen = minim.loadSample("stolen.wav"); 
    enemyPickup = minim.loadSample("enemyPickup.wav"); 
    splosh = minim.loadSample("splosh.wav");
    killed = minim.loadSample("killed.wav"); 
    impact = minim.loadSample("impact.wav") ; 
  }
  
  public void drawStartScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    text("The Jolly Dodger", gameArea.x + gameSize.x/2, 200);
    textSize(30) ;
    text("Press n key to start a new game", gameArea.x + gameSize.x/2,  400);
    fill(0,0,0);
  }
  
  // Screen drawn at the end of every level 
  public void drawLevelCompletedScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    
    if(bossHappinessLevel <= 0){
      gameStage = 3;  
      splosh.trigger();
    }
    else{
      textSize(50); 
      fill(255,255,255);
      textAlign(CENTER,CENTER);
      text("Day " + level + " completed", gameArea.x + gameSize.x/2, gameArea.y + 100);
      textSize(30) ;
      text("PlayerTreasure = " + playerTreasure, gameArea.x + gameSize.x/2, gameArea.y + 200);
      text("Enemy Treasure = " + enemyTreasure, gameArea.x + gameSize.x/2, gameArea.y + 300);
      
      text("Boss Happiness " + bossHappinessLevel + "%", gameArea.x + gameSize.x/2, gameArea.y + 400);
  
      text("Press space key to start the next level", gameArea.x + gameSize.x/2, gameArea.y + 500);
      
      fill(0,0,0);
    }
  }
  
  public void drawDeathScreen(){
    textSize(50); 
    fill(255,255,255);
    textAlign(CENTER,CENTER);
    PVector middle = PVector.add(gameArea, gameSize).div(2); 
     text("GameOver", middle.x,200);
     text("Press space to continue",middle.x , 300 );
     
     if(playerTreasure > oldHighScore){
       oldHighScore = playerTreasure;
     }
     else if(playerTreasure == oldHighScore){
      text("New highscore", middle.x, 400); 
      text(highScore, middle.x, 450);
     }
    fill(0,0,0);
  }
  
  // Draw coloured corners
  public void drawPlayScene(){
  //Draw corners 
  fill(0,255,0);
  arc(gameArea.x,gameArea.y,200,200, 0, HALF_PI);
 
  fill(255,0,0);
  arc(gameArea.x + gameSize.x,gameArea.y,200,200, HALF_PI,PI);
  
  fill(255,165,0);
  arc(gameArea.x + gameSize.x,gameArea.y + gameSize.y,200,200, -PI, -HALF_PI);
  
  fill(255,105,180);
  arc(gameArea.x,gameArea.y + gameSize.y,200,200, -HALF_PI,0);
    
  }
  
  // Setup a new game
  public void newGame(){
    startMusic.pause();
    startMusic.rewind();
    gameMusic.loop();
    pl = new player(new PVector(gameArea.x + gameSize.x -20,gameArea.x + gameSize.y -20),new PVector(0,0),28);
    playerTreasure = 0 ;
    enemyTreasure = 0; 
    level = 0; 
    gameStage = 1; 
    numBalls = 3; 

    newLevel();
}
  
  // Setup a new level
  public void newLevel(){
    pl.playerUpgrade();
    level = level + 1; 
    pl.pos= new PVector(gameArea.x +20,gameArea.y + 20);
    movements = new boolean[4]; 
    pl.detected = false;
    warning.pause();
    warning.rewind();
    lh = new lighthouse();
    collisions = new ArrayList<collision>();
    islandCollisions = new ArrayList<islandCollision>();
    cannons = new ArrayList<projectile>();
    tr = new ArrayList<island> (); 
    //int numTerrains = 3; 
    newRay = new ArrayList<ray>();
    //int numballs = 3;
    objectSizes = 15;
    enemies = new ArrayList<enemy>();
    for (int i = 0; i< numBalls;i++){
      enemies.add(new enemy(getSpawnLocation(),new PVector(random(-3,3),random(-3,3)),getEnemySize()));
      collisions.add(new collision(pl,enemies.get(i))); 
  
      for(int j = 0; j< i; j++){
        collisions.add(new collision(enemies.get(i),enemies.get(j))); 
      }
    }
    
    for(enemy e: enemies){
      islandCollisions.add(new islandCollision(e,tr));
    }
    
    playerCollision = new islandCollision(pl,tr);
    treasureLocations = new ArrayList<PVector>();
    bn = new BinaryTree(new PVector(gameArea.x,gameArea.y), new PVector(gameSize.x,gameSize.y),350,tr,treasureLocations,objectSizes);
    
    while(tr.size() <=5 || treasureLocations.size() <= 2){
        tr.clear();
        treasureLocations.clear();
      
        bn = new BinaryTree(new PVector(gameArea.x,gameArea.y), new PVector(gameSize.x,gameSize.y),350,tr,treasureLocations,objectSizes);
    }
    ArrayList<PVector> corner = new ArrayList<PVector>();
    corner.add(new PVector(gameArea.x,gameArea.y));
    corner.add(new PVector(gameArea.x + gameSize.x,gameArea.y));
    corner.add(new PVector(gameArea.x + gameSize.x,gameArea.y + gameSize.y));
    corner.add(new PVector(gameArea.x,gameArea.y + gameSize.y));
    tr.add(new island(corner));
    
    for(terrain t: tr){
        newRay.add(new ray(tr,t,pl));
    }
    
    for(enemy e: enemies){
      newRay.add(new ray(tr,e,pl));
    }
    newRay.add(new ray(tr,pl,pl));
  
}

// Get random corner spawn loation for enemy 
public PVector getSpawnLocation(){
  int n = rand.nextInt(3);
  if(n==0){
    return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y -5);
  }
  if(n==1){
    return new PVector(gameArea.x + gameSize.x -5 ,gameArea.y+5);
  }
  if(n==2){
    return new PVector(gameArea.x +5,gameArea.y + gameSize.y -5);
  }
    return new PVector(gameArea.x + gameSize.x-5,gameArea.y + gameSize.y -5);
  
}

// Get random size for new enemy 
public float getEnemySize(){
 return random(20,28); 
}
  
}
class shadow extends terrain{
  float angle,size; 
  ArrayList<PVector> allVerteces; 
  
  shadow(ArrayList<PVector> allVerteces,float angle,float size){
    super();
    this.allVerteces = allVerteces;
    this.numberVerteces = verteces.size();
    averageVertex = new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2);
    this.angle = angle; 
    this.size = size; 
  }
  
  public void drawShape(){
    
    if(pl.detected){
      fill(255,0,0); 
    }
    else{
      fill(255,255,0);
    }
    
    noStroke();
    PVector center = new PVector(gameArea.x + (gameSize.x)/2,gameArea.y + (gameSize.y)/2);
    if(size + angle > PI){
      crossesPI(center);
      return; 
    }
    else{
      for(PVector v: allVerteces){
        float ag = atan2(v.y - center.y,v.x - center.x); 
        if(isContained(ag) || PVector.dist(center,v) == 0){
            verteces.add(v);    
        }
      }
      
      numberVerteces = verteces.size();
      averageVertex = center;
      orderVerteces(0,numberVerteces -1);
      
      drawNormal(verteces, center);
    }
    
  }
  
  // Orders and draws beam created by lighthouse
  public void drawNormal(ArrayList<PVector> verteces, PVector center){
    
    beginShape();
    if(size!= TWO_PI){
      vertex(center.x,center.y);
    }  
    for(PVector v: verteces){
          vertex(v.x,v.y);     
    }
    endShape(CLOSE);  
    stroke(0,0,0);
    fill(0,0,0);
  }
  
  // beam passes over PI and - PI 
  public void drawOverPi(terrain ps, terrain neg, PVector center){
    beginShape();
    if(size != TWO_PI){
      vertex(center.x,center.y);
    }
    for(PVector v: ps.verteces){
          vertex(v.x,v.y);     
    }
    for(PVector v: neg.verteces){
          vertex(v.x,v.y);     
    }
    
    endShape(CLOSE);  
    stroke(0,0,0);
    fill(0,0,0);
  }
  
  public boolean isContained(float ag){
    if(ag > angle){
      return (ag - angle) <= size; 
    }
    else{
      return ((PI - angle) + (ag +PI)) <= size ; 
    }
    
  }
  
  // Orders rays when beam passes over pi/ -pi 
  public void crossesPI(PVector center){
   
    terrain posTr = new terrain();
    terrain negTr = new terrain();
    
    for(PVector v: allVerteces){
      float ag = atan2(v.y - center.y,v.x - center.x); 
      if(isContained(ag) || PVector.dist(center,v) == 0){
          if(ag > 0) {
            posTr.verteces.add(v);
          }
          else{
            negTr.verteces.add(v);
          }
      }
    }
    posTr.averageVertex = center;
    negTr.averageVertex = center;
    posTr.numberVerteces = posTr.verteces.size();
    negTr.numberVerteces = negTr.verteces.size();
    
    if(posTr.numberVerteces > 1){
      posTr.orderVerteces(0,posTr.numberVerteces -1);
    }
    if (negTr.numberVerteces > 1){
      negTr.orderVerteces(0,negTr.numberVerteces-1);
    }
    drawOverPi(posTr, negTr, center);
  }
  
  
  
  
}
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
  
  public void getAverageVertex(){
    float  x = 0; 
    float y = 0; 
    for(int i = 0; i < numberVerteces; i++){
      x = x + verteces.get(i).x;
      y = y + verteces.get(i).y;
    }
    averageVertex = new PVector(x/numberVerteces, y/numberVerteces);
  }
  
  public void drawShape(){
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
  public void orderVerteces(int start, int end ){
    if(start < end) {

      int middle = (start + end )/2;

      orderVerteces( start, middle);  
      orderVerteces( middle + 1, end);
      mergeSort(start, middle, end);
    }
  }
  
  // Merge sort algorithm 
  public void  mergeSort( int start, int middle, int end){
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
  
  public float getAngle(PVector point){
    PVector diff = PVector.sub(point, averageVertex);
     float an = atan2(diff.y,diff.x); 
     return an; 
  }
  
}
  public void settings() {  size(966, 568); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
