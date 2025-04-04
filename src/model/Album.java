package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo album in the Photo App.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<Photo> photos;
    
    /**
     * Creates a new album with the specified name.
     * 
     * @param name The name of the album.
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Gets the name of this album.
     * 
     * @return The album name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this album.
     * 
     * @param name The new album name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the list of photos in this album.
     * 
     * @return The list of photos.
     */
    public List<Photo> getPhotos() {
        return photos;
    }
    
    public Photo getCoverPhoto() {
        if (photos.isEmpty()) {
            return null;
        }
        return photos.get(0);
    }
    /**
     * Adds a photo to this album.
     * 
     * @param photo The photo to add.
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
    
    /**
     * Removes a photo from this album.
     * 
     * @param photo The photo to remove.
     * @return true if the photo was removed, false otherwise.
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }
    
    /**
     * Gets the number of photos in this album.
     * 
     * @return The number of photos.
     */
    public int getPhotoCount() {
        return photos.size();
    }
    
    /**
     * Gets the earliest date among all photos in this album.
     * 
     * @return The earliest date, or null if the album is empty.
     */
    public LocalDateTime getEarliestDate() {
        if (photos.isEmpty()) {
            return null;
        }
        
        LocalDateTime earliest = photos.get(0).getDateTime();
        for (Photo photo : photos) {
            if (photo.getDateTime().isBefore(earliest)) {
                earliest = photo.getDateTime();
            }
        }
        return earliest;
    }
    
    /**
     * Gets the latest date among all photos in this album.
     * 
     * @return The latest date, or null if the album is empty.
     */
    public LocalDateTime getLatestDate() {
        if (photos.isEmpty()) {
            return null;
        }
        
        LocalDateTime latest = photos.get(0).getDateTime();
        for (Photo photo : photos) {
            if (photo.getDateTime().isAfter(latest)) {
                latest = photo.getDateTime();
            }
        }
        return latest;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album other = (Album) obj;
        return name.equals(other.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
}