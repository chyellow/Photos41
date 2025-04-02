package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a photo in the Photo App.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String caption;
    private LocalDateTime dateTime;
    private Map<String, String> tags; // tag type -> tag value
    
    /**
     * Creates a new photo with the specified file path.
     * 
     * @param filePath The path to the photo file.
     * @param dateTime The date and time when the photo was taken.
     */
    public Photo(String filePath, LocalDateTime dateTime) {
        this.filePath = filePath;
        this.dateTime = dateTime;
        this.caption = "";
        this.tags = new HashMap<>();
    }
    
    /**
     * Gets the file path of this photo.
     * 
     * @return The file path.
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Gets the caption of this photo.
     * 
     * @return The caption.
     */
    public String getCaption() {
        return caption;
    }
    
    /**
     * Sets the caption of this photo.
     * 
     * @param caption The new caption.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /**
     * Gets the date and time when this photo was taken.
     * 
     * @return The date and time.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    /**
     * Gets the tags associated with this photo.
     * 
     * @return The map of tag types to tag values.
     */
    public Map<String, String> getTags() {
        return tags;
    }
    
    /**
     * Adds a tag to this photo.
     * 
     * @param tagType The type of the tag.
     * @param tagValue The value of the tag.
     */
    public void addTag(String tagType, String tagValue) {
        tags.put(tagType, tagValue);
    }
    
    /**
     * Removes a tag from this photo.
     * 
     * @param tagType The type of the tag to remove.
     * @return true if the tag was removed, false otherwise.
     */
    public boolean removeTag(String tagType) {
        return tags.remove(tagType) != null;
    }
    
    /**
     * Checks if this photo has a tag of the specified type.
     * 
     * @param tagType The type of the tag to check.
     * @return true if the photo has a tag of the specified type, false otherwise.
     */
    public boolean hasTag(String tagType) {
        return tags.containsKey(tagType);
    }
    
    /**
     * Gets the value of a tag with the specified type.
     * 
     * @param tagType The type of the tag.
     * @return The value of the tag, or null if no such tag exists.
     */
    public String getTagValue(String tagType) {
        return tags.get(tagType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo other = (Photo) obj;
        return filePath.equals(other.filePath);
    }
    
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}