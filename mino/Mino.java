package mino;

import tetris.GamePanel;
import tetris.KeyHandler;
import tetris.PlayManager;

import java.awt.*;

public class Mino {

    public Block b []  = new Block[4];
    public Block tempB[] = new Block[4];

    int autoDropCounter = 0;

    public int direction = 1;
    boolean leftCollision,rightCollision,bottomCollision;
    public boolean active = true;

    // các biến để tạo khoảng cách thời gian giữa mino mới và mino cũ
    public boolean deactivating;
    int deactivateCounter =0;


    public void create(Color c){
        b[0]= new Block(c);
        b[1]= new Block(c);
        b[2]= new Block(c);
        b[3]= new Block(c);
        tempB[0]= new Block(c);
        tempB[1]= new Block(c);
        tempB[2]= new Block(c);
        tempB[3]= new Block(c);

    }

    public void setXY(int x, int y){

    }

    public void updateXY(int direction){

        checkRotationCollision();


        if (leftCollision == false && rightCollision == false && bottomCollision == false){
         this.direction=direction;

         // lưu trữ vị trí ban đầu nếu va chạm xảy ra và ta cần hủy bỏ trạng thái để reset
        // sử dụng mảng temp để lưu trữ
         b[0].x = tempB[0].x;
         b[0].y = tempB[0].y;
         b[1].x = tempB[1].x;
         b[1].y = tempB[1].y;
         b[2].x = tempB[2].x;
         b[2].y = tempB[2].y;
         b[3].x = tempB[3].x;
         b[3].y = tempB[3].y;                                                             }

    }

    public void getDirection1(){

    }public void getDirection2(){

    }public void getDirection3(){

    }public void getDirection4(){
    }
    public void checkMovementCollision(){

            leftCollision =false;
            rightCollision =false;
            bottomCollision =false;

            //kiểm tra va chạm
        checkStaticBlockCollision();

            // kiểm tra va chạm khung tường bên trái

        for (int i=0 ; i< b.length ; i++){
            if (b[i].x == PlayManager.left_x){
                leftCollision = true;
            }
        }

        // khung tường bên phải

        for (int i=0 ; i< b.length ; i++){
            if (b[i].x + Block.SIZE == PlayManager.right_x){
                rightCollision = true;
            }
        }
        // khung tường đáy
        for (int i=0 ; i< b.length ; i++){
            if (b[i].y  + Block.SIZE == PlayManager.bottom_y){
                bottomCollision = true;
            }
        }

    }
    public void checkRotationCollision(){

        leftCollision =false;
        rightCollision =false;
        bottomCollision =false;

        //kiểm tra va chạm
        checkStaticBlockCollision();

        // kiểm tra va chạm khung tường bên trái

        for (int i=0 ; i< b.length ; i++){
            if (tempB[i].x < PlayManager.left_x){ // x bên trái lớn hơn temp .x
                leftCollision = true;
            }
        }

        // kiểm tra va chạm khung tường bên phải

        for (int i=0 ; i< b.length ; i++){
            if (tempB[i].x + Block.SIZE > PlayManager.right_x){ // x bên phải nhỏ hơn temp.x
                rightCollision = true;
            }
        }
        // kiểm tra va chạm khung tường đáy
        for (int i=0 ; i< b.length ; i++){
            if (tempB[i].y  + Block.SIZE > PlayManager.bottom_y){ // y đáy nhỏ hơn temp .y
                bottomCollision = true;
            }
        }

    }

    private void checkStaticBlockCollision(){

        for (int i =0 ; i< PlayManager.staticBlocks.size() ;i++){
            // lấy mỗi khối trục x và y
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;


        // check down

        for (int j =0 ; j < b.length ; j++){
            if(b[j].y + Block.SIZE == targetY && b[j].x == targetX){
                bottomCollision = true;
            }

        }
        // trái
        for (int j =0 ; j<b.length ; j++){
            if(b[j].x - Block.SIZE ==  targetX && b[j].y == targetY){
                leftCollision = true;
            }
        }
        // phải
        for (int j =0 ; j<b.length ; j++){
            if(b[j].x + Block.SIZE ==  targetX && b[j].y == targetY){
                rightCollision = true;
            }
        }
        }
    }
    public void update(){


        if (deactivating ){
            deactivating();
        }

        if(KeyHandler.upPressed){
            //xoay mino

            switch (direction){
                // nếu hướng hiện tại là 1 thì sẽ xoay theo thứ tự sau
                case 1:
                    getDirection2(); break; // mino quay mỗi lần phím w được nhấn
                case 2:
                    getDirection3();break;
                case 3:
                    getDirection4();break;
                case 4:
                    getDirection1();break;

            }

            KeyHandler.upPressed = false;
            GamePanel.se.play(3,false);

        }

        checkMovementCollision();

        if(KeyHandler.downPressed) {

            if(bottomCollision == false)
            {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                // khi di chuyển xuống đặt lại bộ đếm thả tự động
            autoDropCounter =0;
            // reset
            KeyHandler.downPressed =false;
            }
                                    }
        if(KeyHandler.leftPressed){

            if (leftCollision ==false ){


             b[0].x -= Block.SIZE;
             b[1].x -= Block.SIZE;
             b[2].x -= Block.SIZE;
             b[3].x -= Block.SIZE;

            KeyHandler.leftPressed =false;}
        }
        if(KeyHandler.rightPressed) {

            if (rightCollision == false) {


                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;

                KeyHandler.rightPressed = false;
            }
        }

        if (bottomCollision){
            if(deactivating == false){
                GamePanel.se.play(4,false);
            }
            deactivating = true;
        }
        else{


        autoDropCounter ++;
        if(autoDropCounter == PlayManager.dropInterval){
            // mino goes doww
            b[0].y +=Block.SIZE;
            b[1].y +=Block.SIZE;
            b[2].y +=Block.SIZE;
            b[3].y +=Block.SIZE;
            autoDropCounter =0;

                                                    }
        }
    }

    private void deactivating() {

        deactivateCounter ++;
        // chờ 45 frame cho đến khi dừng hoạt động
        if(deactivateCounter ==45){

            deactivateCounter =0;
            checkMovementCollision(); // kiểm tra xem đáy có còn chạm không

            // nếu đáy vẫn đập sau 45 khung hình, thì mino tắt
            if(bottomCollision){
                active = false;
            }
        }
    }

    public void draw(Graphics2D g2){
           int margin =2;
           g2.setColor(b[0].c);
           g2.fillRect(b[0].x+margin,b[0].y+margin,Block.SIZE-(margin*2), Block.SIZE-(margin*2));
           g2.fillRect(b[1].x+margin,b[1].y+margin,Block.SIZE-(margin*2), Block.SIZE-(margin*2));
           g2.fillRect(b[2].x+margin,b[2].y+margin,Block.SIZE-(margin*2), Block.SIZE-(margin*2));
           g2.fillRect(b[3].x+margin,b[3].y+margin,Block.SIZE-(margin*2), Block.SIZE-(margin*2));
    }
}
