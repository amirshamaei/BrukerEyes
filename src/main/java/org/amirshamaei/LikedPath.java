package org.amirshamaei;

public class LikedPath {
        String path = new String();
        String name = new String();
        String descp = new String();

    public LikedPath(String path, String name, String descp) {
        this.path = path;
        this.name = name;
        this.descp = descp;
    }

    public LikedPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
