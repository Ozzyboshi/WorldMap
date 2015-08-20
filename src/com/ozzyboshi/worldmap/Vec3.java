/*
The MIT License (MIT)

Copyright (c) 2015, Alessio Garzi <gun101@email.it>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/ 

package com.ozzyboshi.worldmap;

public class Vec3 {
	
	private double X,Y,Z;
	
    public Vec3(double x,double y,double z)
    {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
   public double dot(Vec3 vec) {
        return (X*vec.X) + (Y*vec.Y) + (Z*vec.Z);
    }
    
    public Vec3 add(Vec3 vec) {
        return new Vec3(X + vec.X, Y + vec.Y, Z + vec.Z);
    }
    
    public void normalize() {
    	double to=1;
        double invLength = to / length();
        this.X *= invLength;
        this.Y *= invLength;
        this.Z *= invLength;
    }
    
    private double lengthSquared() {
        return Math.pow(this.X, 2) + Math.pow(this.Y, 2) + Math.pow(this.Z, 2);
    }
    
    private double length() {
        return Math.sqrt(lengthSquared());
    }
    
    public double getX() {
    	return X;
    }
    public double getY() {
    	return Y;
    }
    public double getZ() {
    	return Z;
    }
}
