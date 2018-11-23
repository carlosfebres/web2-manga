package Interfaces;

import Exceptions.ModelNotFound;

public interface Model {
	boolean save();
	boolean delete();
	void fetch() throws ModelNotFound;
}
