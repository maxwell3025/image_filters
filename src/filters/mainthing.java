package filters;

import javax.swing.JOptionPane;

public class mainthing {

	public static void main(String[] args) {
		String[] options = { "rainbow snow thing", "galaxy sim", "floating blobs", "waves", "chain", "mold growth", "grav sim", "pong", "not working", "filter1", "filter2","filter3","filter4","filter5","filter6","imagegen1","imagegen2"};
		int option = JOptionPane.showOptionDialog(null, "choose your program, also, for most, if it isn't doing anything, hit space, also, you can hit enter for some the make everthing move.", null, JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, null);
		if(option ==0){
			anim1.main(new String[0]);
		}else if(option ==1){
		anim2.main(new String[0]);
			
		}else if(option ==2){
		anim3.main(new String[0]);
			
		}else if(option ==3){
		anim4.main(new String[0]);
			
		}else if(option ==4){
		anim5.main(new String[0]);
			
		}else if(option ==5){
		anim6.main(new String[0]);
			
		}else if(option ==6){
		anim7.main(new String[0]);
			
		}else if(option ==7){
		anim8.main(new String[0]);
			
		}else if(option ==8){
		anim9.main(new String[0]);
			
		}else if(option ==9){
		filter1.main(new String[0]);
			
		}else if(option ==10){
		filter2.main(new String[0]);
			
		}else if(option ==11){
		filter3.main(new String[0]);
			
		}else if(option ==12){
		filter4.main(new String[0]);
			
		}else if(option ==13){
		filter5.main(new String[0]);
			
		}else if(option ==14){
		filter6.main(new String[0]);
			
		}else if(option ==15){
		imagegen1.main(new String[0]);
			
		}else if(option ==16){
		imagegen2.main(new String[0]);
			
		}
		
		

	}

}
