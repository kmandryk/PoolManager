package tournament;

import java.util.Vector;

/*
 * This class is intended to take snapshots of the Tournament in order to revert
 * The tournament to a previous state. This class will store tournament data which
 * can replace current tournament data.
 */
public class Snapshot {

	private Vector<Tournament> snapList;
	public int vectorPointer;

	public Snapshot() {
		snapList = new Vector<Tournament>();
		vectorPointer = -1;
	}

	/** This function intelligently decides whether adding a snapshot is necessary,
	 *  based on the position of the vector Pointer. This allows callers to be 
	 *  ignorant of the state of the snapshot vector.
	 * @param tournament
	 */
	public void addSnapshot(Tournament tournament) {
		vectorPointer++;
		if (hasOneNext()) {
			int iter = vectorPointer;
			while (true) {
				if (hasOneNext()) {
					snapList.remove(++iter);
				} else break;
			}
		} else{
		// need to copy the tournament and push it to the stack
		Tournament nt = new Tournament(tournament.getThisWeek(),
				tournament.getPlayerSet());
		Tournament.copy(tournament, nt);
		snapList.add(nt);
		}
		return;
	}

	public Tournament revert(Tournament tournament) {
		if(vectorPointer <= 0){
			vectorPointer = -1;
			return snapList.get(0);
		}
		if (hasTwoNext() || hasOneNext()) {
			return snapList.get(vectorPointer--);
		} else {
			addSnapshot(tournament);
			vectorPointer -= 2;
			return snapList.get(vectorPointer + 1);
		}
	}

	public void remove() {
		snapList.remove(vectorPointer--);
	}

	public boolean isEmpty() {
		return (vectorPointer <= 0);
	}

	/** These functions refer to the position of the vector Pointer **/
	public boolean hasOneNext() {
		try {
			snapList.get(vectorPointer + 1);
			return true;
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
	}

	public boolean hasTwoNext() {
		try {
			snapList.get(vectorPointer + 2);
			return true;
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			return false;
		}
	}

	public Tournament Next() {
		if (hasTwoNext()) {
			vectorPointer++;
			return snapList.get(vectorPointer + 1);
		} else if (hasOneNext()) {
			return snapList.get(vectorPointer + 1);
		} else
			return null;
	}
}
