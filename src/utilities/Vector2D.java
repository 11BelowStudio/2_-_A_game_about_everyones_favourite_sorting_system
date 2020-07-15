package utilities;

import java.awt.*;

//le ce218 sample code has arrived (template provided by Dr Dimitri Ognibene)

//methods implemente and class enhanced by me

// mutable 2D vectors
public final class Vector2D {
    public double x, y;

    // constructor for zero vector
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    // constructor for vector with given coordinates
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //constructor for a vector with same co-ords as a Point
    public Vector2D(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    // constructor that copies the argument vector
    public Vector2D(Vector2D v) {
        double tempX = v.x;
        double tempY = v.y;
        this.x = tempX;
        this.y = tempY;
    }

    // set coordinates
    public Vector2D set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    // set coordinates based on argument vector
    public Vector2D set(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    // compare for equality (note Object type argument)
    public boolean equals(Object o) {
        if (o instanceof Vector2D){
            //if the other object is a Vector2D, compares x and y of this Vector2D and that Vector2D
            Vector2D v =(Vector2D) o;
            return (this.x == v.x && this.y == v.y);
        }
        return false;

    }

    // String for displaying vector as text
    public String toString() {
        return "("+x + ", "  + y+")";

    }

    //  magnitude (= "length") of this vector
    public double mag() {
        return (Math.hypot(x,y));
        //hypotenuse of triangle basically
    }

    // angle between vector and horizontal axis in radians in range [-PI,PI]
// can be calculated using Math.atan2
    public double angle() {
        return Math.atan2(y,x);
    }

    // angle between this vector and another vector in range [-PI,PI]
    public double angle(Vector2D other) {
        //finding difference between the angles
        double result = other.angle() - this.angle();
        //wrapping the result around if it's outside range [-PI,PI] to keep it in range
        if (result < -Math.PI){
            result += 2*Math.PI;
            //2pi added if it's below -pi
        } else if (result > Math.PI){
            result -= 2* Math.PI;
            //2pi removed if it's above pi
        }
        return result;
    }

    // add argument vector
    public Vector2D add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }
    public static Vector2D add(Vector2D v1, Vector2D v2){
        Vector2D result = new Vector2D(v1);
        return result.add(v2);
    }

    // add values to coordinates
    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public static Vector2D add(Vector2D v, double x, double y){
        Vector2D result = new Vector2D(v);
        return result.add(x,y);
    }

    // weighted add - surprisingly useful (adds v but multiplied by the factor)
    public Vector2D addScaled(Vector2D v, double fac) {
        this.x += (v.x*fac);
        this.y += (v.y*fac);
        return this;
    }
    public static Vector2D addScaled(Vector2D v1, Vector2D v2, double fac){
        Vector2D result = new Vector2D(v1);
        return result.addScaled(v2,fac);
    }

    // subtract argument vector (subtracts that vector from this)
    public Vector2D subtract(Vector2D v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    public static Vector2D subtract(Vector2D v1, Vector2D v2){
        Vector2D result = new Vector2D(v1);
        return result.subtract(v2);
    }

    // subtract values from coordinates (subtracts stuff from the respective parameter)
    public Vector2D subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    public static Vector2D subtract(Vector2D v, double x, double y){
        Vector2D result = new Vector2D(v);
        return result.subtract(x,y);
    }

    // multiply with factor (everything divided by factor)
    public Vector2D mult(double fac) {
        this.x = x*fac;
        this.y = y*fac;
        return this;
    }

    public Vector2D multX(double xFac){
        this.x *= xFac;
        return this;
    }
    public static Vector2D mult(Vector2D v, double fac){
        Vector2D result = new Vector2D(v);
        return result.mult(fac);
    }

    // rotate by angle given in radians
    //basically scalar rotation
    public Vector2D rotate(double angle) {
        double tempX = x;
        double tempY = y;
        x = (tempX * Math.cos(angle)) - (tempY * Math.sin(angle));
        y = (tempX * Math.sin(angle)) + (tempY * Math.cos(angle));
        return this;
    }
    public static Vector2D rotate(Vector2D v, double angle){
        Vector2D result = new Vector2D(v);
        return result.rotate(angle);
    }

    // "dot product" ("scalar product") with argument vector
    public double dot(Vector2D v) { return ((this.x*v.x)+(this.y*v.y)); }

    // distance to argument vector
    //Euclidean distance formula (which, for 2d planes, is pretty much pythagoras' theorem)
    public double dist(Vector2D v) { return (Math.hypot((x-v.x),(y-v.y))); }

    public double xDist(Vector2D v) {
        double result = x - v.x;
        if (result < 0){
            result *= -1;
        }
        return result;
    }

    public double xDist(Vector2D v, double wrapBound){
        return (xDist(v) + wrapBound) % wrapBound;
    }

    public double yDist(Vector2D v) {
        double result = y - v.y;
        if (result < 0){
            result *= -1;
        }
        return result;
    }

    public double yDist(Vector2D v, double wrapBound){
        return (yDist(v) + wrapBound) % wrapBound;
    }

    // normalise vector so that magnitude becomes 1
    //basically divides x and y by mag so mag effectively becomes 1
    //unless the magnitude is 0 at which case it can't really do anything
    public Vector2D normalise() {
        double currentMag = this.mag();
        if (currentMag != 0) {
            this.x = x / currentMag;
            this.y = y / currentMag;
        }
        return this;
    }
    public static Vector2D normalise(Vector2D v){
        Vector2D result = new Vector2D(v);
        return result.normalise();
    }
    //setting magnitude of this vector
    public Vector2D setMag(double newMag) {
        this.set(polar(this.angle(),newMag));
        return this;
    }
    //getting a vector thats the same as the  argument but with the new magnitude
    public static Vector2D setMag(Vector2D v, double newMag){ return polar(v.angle(),newMag); }

    // wrap-around operation, assumes w> 0 and h>0
    public Vector2D wrap(double w, double h) {
        this.wrapX(w);
        this.wrapY(h);
        return this;
    }
    public static Vector2D wrap(Vector2D v, double w, double h){
        Vector2D result = new Vector2D(v);
        return result.wrap(w,h);
    }

    public Vector2D wrapX(double w){
        this.x = (x+w) % w;
        //pretty much gets the remainder of x plus width over width (ensures x is under width)
        return  this;
    }

    public Vector2D wrapY(double h){
        this.y = (y+h) % h;
        //ditto but for y and height instead
        return this;
    }

    public Vector2D capMag(double maxMag){
        if (maxMag < 0){
            maxMag *= -1;
            //ensuring a positive maximum mag is used
        }
        if (this.mag() > maxMag){
            this.setMag(maxMag);
            //mag is capped at maximum if it's bigger than it
        }
        return this;
    }

    public Vector2D capXY(double maxX, double maxY){
        //capping the X and Y values of a Vector2D
        //arguments must be positive. if x and y are negative, they will be compared against the inverse maxX/maxY.
        capX(maxX);
        capY(maxY);
        return this;
    }

    public Vector2D capXY(double maxXY){
        return capXY(maxXY,maxXY);
    }

    public Vector2D capX(double maxX){
        //capping the X value of a Vector2D.
        //if x is negative, it's compared to the inverse of maxX.
        if (x > 0){
            if (x > maxX){
                x = maxX;
            }
        } else{
            if (x < -maxX){
                x = -maxX;
            }
        }
        return this;
    }

    public Vector2D capY(double maxY){
        //capping the Y value of a Vector2D.
        //if y is negative, it's compared to the inverse of maxY.
        if (y > 0){
            if (y > maxY){
                y = maxY;
            }
        } else{
            if (y < -maxY){
                y = -maxY;
            }
        }
        return this;
    }

    // construct vector with given polar coordinates
    public static Vector2D polar(double angle, double mag) {
        return new Vector2D(mag*Math.cos(angle),mag*Math.sin(angle));
    }

    //like polar for situations where a random angle is needed instead of a given angle
    public static Vector2D polarWithRandomAngle(double mag){
        return Vector2D.polar(Math.random()*(2*Math.PI),mag);
    }

    //rotation needed to get from this vector's angle to targetVector's angle
    public double getAngleTo(Vector2D targetVector){
        double xAngle = targetVector.x - x;
        double yAngle = targetVector.y - y;
        return Math.atan2(yAngle,xAngle);
    }

    //vector between this vector's co-ordinates to the target vector's co-ordinates
    public Vector2D getVectorTo(Vector2D targetVector){ return polar(getAngleTo(targetVector),dist(targetVector)); }
    //ditto but static instead
    public static Vector2D getVectorTo(Vector2D fromThis, Vector2D toThis){ return fromThis.getVectorTo(toThis); }

    public double getAngleTo(Vector2D v, double w, double h){
        double xAngle = v.x - x;
        double yAngle = v.y - y;
        double w2 = w/2;
        double h2 = h/2;
        if (xAngle > w2){
            xAngle =- w2;
        } else if(xAngle < -w2){
            xAngle += w2;
        }

        if (yAngle > h2){
            yAngle =- h2;
        } else if(yAngle < -h2){
            yAngle += h2;
        }
        return Math.atan2(yAngle,xAngle);

    }

    //get the vector from this to the other one, wrapping around by w and h
    public Vector2D getVectorTo(Vector2D toThis, double w, double h){ return polar(getAngleTo(toThis,w,h),toThis.dist(this)); }
    //ditto but its a static method
    public static Vector2D getVectorTo(Vector2D fromThis, Vector2D toThis, double w, double h){ return fromThis.getVectorTo(toThis,w,h); }

    //projection of this vector in some direction
    public Vector2D proj(Vector2D d){
        Vector2D result = new Vector2D(d);
        result.mult(this.dot(d));
        return result;
    }

    //random vector going in some direction from the origin
    public static Vector2D randomVectorFromOrigin(Vector2D origin, double minDist, double rangeDist){
        Vector2D fromOrigin = polar(Math.toRadians(Math.random()*360),(Math.random()*rangeDist)+minDist);
        fromOrigin.add(origin);
        return fromOrigin;
    }

    //random vector pointing to an origin
    public static Vector2D randomVectorPointingTo(Vector2D pointToThis, double mag){
        Vector2D vectorFromOrigin = polar(Math.toRadians(Math.random()*360),mag);
        vectorFromOrigin.add(pointToThis);
        double xAngle = pointToThis.x - vectorFromOrigin.x;
        double yAngle = pointToThis.y - vectorFromOrigin.y;
        return polar(Math.atan2(yAngle,xAngle),mag);
    }

    //just a random vector
    public static Vector2D getRandomVector(double maxX, double maxY){
        return new Vector2D(Math.random()*maxX,Math.random()*maxY);
    }

    //inverts this vector
    public Vector2D inverse(){
        x *= -1;
        y *= -1;
        return this;
    }

    //inverts X
    public Vector2D invertX(){
        x *= -1;
        return this;
    }

    //inverts Y
    public Vector2D invertY(){
        y *= -1;
        return this;
    }

    //get the inverse of the specified vector
    public static Vector2D inverse(Vector2D v){
        Vector2D result = new Vector2D(v);
        return result.inverse();
    }

    public Point toPoint(){ return new Point((int)x,(int)y); }

    public static Vector2D getVectorToPoint(Vector2D origin, Point p){ return origin.getVectorTo(new Vector2D(p)); }



    //and now, collision stuff
    public Vector2D getCollisionVector(Vector2D other){
        return new Vector2D(other).subtract(this).normalise();
    }
    public static Vector2D getCollisionVector(Vector2D v1, Vector2D v2){ return v1.getCollisionVector(v2); }

    public Vector2D getTangent(){ return new Vector2D(-y,x); }
    public static Vector2D getTangent(Vector2D collision){ return new Vector2D(-collision.y, collision.x); }

    public static Vector2D getCollisionVelocity(Vector2D thisV, Vector2D otherV, Vector2D collision, Vector2D tangent){
        return new Vector2D((thisV.proj(tangent)).add(otherV.proj(collision)));
    }

    public Vector2D getCollisionVelocity(Vector2D thisP, Vector2D otherP, Vector2D otherV){
        Vector2D coll = thisP.getCollisionVector(otherP);
        return this.set((this.proj(coll.getTangent())).add(otherV.proj(coll)));
    }

    public boolean isNull(){
        return (x == 0 && y == 0);
    }

    public boolean isInBounds(double minX, double maxX, double minY, double maxY){
        return ((x > minX) && (x < maxX) && (y > minY) && (y < maxY));
    }
}