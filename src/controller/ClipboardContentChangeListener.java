package controller;

import java.awt.datatransfer.Transferable;
import java.util.EventListener;

public interface ClipboardContentChangeListener extends EventListener{

	public void contentChanged(Transferable content);
	
}
