package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the Photo App.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private List<Album> albums;
    
    /**
     * Creates a new user with the specified username.
     * 
     * @param username The username for this user.
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }
    
    /**
     * Gets the username of this user.
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the list of albums belonging to this user.
     * 
     * @return The list of albums.
     */
    public List<Album> getAlbums() {
        return albums;
    }
    
    /**
     * Adds a new album to this user's collection.
     * 
     * @param album The album to add.
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }
    
    /**
     * Removes an album from this user's collection.
     * 
     * @param album The album to remove.
     * @return true if the album was removed, false otherwise.
     */
    public boolean removeAlbum(Album album) {
        return albums.remove(album);
    }
    
    /**
     * Checks if this user has an album with the specified name.
     * 
     * @param albumName The name of the album to check.
     * @return true if an album with the specified name exists, false otherwise.
     */
    public boolean hasAlbumWithName(String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets an album by its name.
     * 
     * @param albumName The name of the album to retrieve.
     * @return The album with the specified name, or null if no such album exists.
     */
    public Album getAlbumByName(String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return username.equals(other.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
    
    @Override
    public String toString() {
        return username;
    }
}