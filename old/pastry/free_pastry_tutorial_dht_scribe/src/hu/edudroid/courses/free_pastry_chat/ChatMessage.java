package hu.edudroid.courses.free_pastry_chat;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class ChatMessage implements Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 2808438087302335977L;
Id from;
  Id to;
  String body;

  public ChatMessage(Id from, Id to, String body) {
    this.from = from;
    this.to = to;
    this.body = body;
  }
  
  public String toString() {
    return "Msg "+from+" >>>> "+to + " " + body;
  }

  /**
   * Use low priority to prevent interference with overlay maintenance traffic.
   */
  public int getPriority() {
    return Message.LOW_PRIORITY;
  }
}




