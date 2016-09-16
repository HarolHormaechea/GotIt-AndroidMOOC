package edu.hh.repositories;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import retrofit.mime.TypedFile;



public class ImageSave {
	private Path imagesDirectory = Paths.get("pictures");
	
	public ImageSave(){
		if(!Files.exists(imagesDirectory)){
			try {
				Files.createDirectories(imagesDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Private helper method for resolving file paths
	private Path getPicturePath(String username){
		assert(username != null && username.length() > 3);
		return imagesDirectory.resolve("profile_image_"+username+".jpg");
	}
	
	/**
	 * This method returns true if the specified Video has binary
	 * data stored on the file system.
	 * 
	 * @param v
	 * @return
	 */
	public boolean hasProfilePicture(String username){
		Path source = getPicturePath(username);
		return Files.exists(source);
	}
	
	/**
	 * 
	 */
	public TypedFile copyImageData(String username) throws IOException {
		Path source = getPicturePath(username);
		if(!Files.exists(source)){
			throw new FileNotFoundException("Profile image not set for:"+username);
		}
		TypedFile response = new TypedFile("image/jpg", source.toFile());
		return response;
	}
	
	/**
	 * This method reads all of the data in the provided InputStream and stores
	 * it on the file system. The data is associated with the user image that
	 * is provided by the caller.
	 * 
	 */
	public void saveImageData(String username, InputStream image) throws IOException{
		assert(image != null);
		
		Path target = getPicturePath(username);
		Files.copy(image, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
