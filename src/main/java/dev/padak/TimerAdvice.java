package dev.padak;

import net.bytebuddy.asm.Advice;

public class TimerAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    static long onEnter(
                        //,@Advice.FieldValue(value = "name", readOnly = false) String nameField
        ){

//        if(ary != null) {
//            for(int i =0 ; i < ary.length ; i++){
//                System.out.println("Argument: " + i + " is " + ary[i]);
//            }
//        }

//        System.out.println("Origin :" + origin);
//        System.out.println("Detailed Origin :" + detailedOrigin);

        //nameField = "Jack";
        return System.nanoTime();

    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void onExit(
                    @Advice.Enter long time,
                    @Advice.Origin String origin,
                    @Advice.This Object thisObject,
                    @Advice.Origin("#m") String detailedOrigin,
                    @Advice.Thrown Throwable exception) {

        System.out.println("<><><><><><><> START exit method [" + origin + "]. . .");
//        System.out.println(
//                "<><><><><><><> " +
//                        " Method " + origin +
//                        ", This: " + thisObject.getClass().getName() +
//                        ", Detailed origin: " + detailedOrigin + " " +
//                        ", Throwable: " + exception + " " +
//                        " -> Execution Time: " + (System.nanoTime() - time) + " nano seconds");

        //showStuff(time, origin, thisObject, detailedOrigin, exception);

        try {
            showStuff();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.out.println("<><><><><><><> END exit method [" + origin + "]. . .");
    }

    public static void showStuff() {
        System.out.println("<><><><><><><> AHI VAMO'");
    }

    private static void showStuff( long time,
                                   String origin,
                                   Object thisObject,
                                   String detailedOrigin,
                                   Throwable exception) {

        final boolean isSelectedClass = false;//isSelectedClass(thisObject);


        System.out.println(
                "° ° ° ° ° ° onExit: " +
                        "isSelectedClass: " + isSelectedClass +
                        ", time: " + time +
                        ", latency: " + (System.nanoTime() - time) +
                        ", origin: " + thisObject.getClass() +
                        ", detailedOrigin: " + detailedOrigin +
                        ", exception: " + exception
        );
    }
}
