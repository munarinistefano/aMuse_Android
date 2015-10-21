package com.unitn.amuse;

import android.os.Parcel;
import android.os.Parcelable;

public class Work implements Parcelable {
	 int idWork;
	 String exhibition = null;
	 String name = null;
	 String author=null;
	 String description;
	 String imgWork;
	 boolean selected = false;
	  
	 public Work(int idWork, String name, String author, String exhibition,  String description, String imgWork, boolean selected) {
		 super();
		 this.idWork=idWork;
		 this.name = name;
		 this.author=author;
		 this.exhibition = exhibition;
		 this.description= description;
		 this.imgWork=imgWork;
		 this.selected = selected;
	 }
	  
	 public Work(Parcel in) {
		 idWork = in.readInt();
		 exhibition = in.readString();
		 name = in.readString();
		 author=in.readString();
		 description = in.readString();
		 imgWork = in.readString();
		 selected = in.readByte() == 1;
	}

	public String getExhibition() {
		return exhibition;
	}
	
	public void setExhibition(String exhibition) {
		this.exhibition = exhibition;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	 
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	 
	public String getImgWork() {
		return imgWork;
	}
	
	public void setImgWork(String imgWork) {
		this.imgWork = imgWork;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idWork);
		dest.writeString(exhibition);
		dest.writeString(name);
		dest.writeString(author);
		dest.writeString(description);
		dest.writeString(imgWork);
		dest.writeByte((byte) (selected ? 1 : 0));
	}
	
	public static final Parcelable.Creator<Work> CREATOR = new Parcelable.Creator<Work>() {
		public Work createFromParcel(Parcel in) {
			return new Work(in);
		}

		@Override
		public Work[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	};
}