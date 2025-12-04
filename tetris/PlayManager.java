package tetris;


// khu vực chơi
// quản lý các khối tetromino
// xử lý hành động chơi trò chơi
// xóa các dòng, thêm điểm

import mino.*;

import java.awt.*;
import java.awt.Graphics2D;
import java.util.ArrayList;

import java.util.Random;

public class PlayManager {

    // 25 *12
    // 25 * 22
    final int WIDTH = 300;
    final int HEIGHT = 550;

    public static int right_x;
    public static int left_x;
    public static int top_y;
    public static int bottom_y;

    // MINO
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    Mino nextMino;  // tạo ra một mino khác và x và y của nó bên dưới
    final int NEXT_MINO_X;
    final int NEXT_MINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>(); //đặt mino không hoạt động vào khối tĩnh khác

    public static int dropInterval = 40;
    public boolean gameOver;

    // Các hiệu ứng

    boolean effectsCounterCOn;
    boolean effectsCounterLOn;

    int effectCounterC;
    int effectCounterL;
    ArrayList<Integer> effectY = new ArrayList<>();
    ArrayList<Integer> effectX = new ArrayList<>();

    int level = 1;
    int lines;
    int column;
    int score;


    public PlayManager() {


        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2); // 1150/2  - 300/2
        right_x = left_x + WIDTH;
        top_y = 30;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXT_MINO_X = right_x + 180;
        NEXT_MINO_Y = top_y + 450;


        // thiết lập khối mino đầu tiên

        currentMino = minoPicker();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = new Mino_Bar();
        nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
    }
    private Mino minoPicker() {
        Mino mino = null;
        int i = new Random().nextInt(7); // sử dụng random để tạo mino (0-6)

        mino = switch (i) {
            case 0 -> new Mino_L1();
            case 1 -> new Mino_L2();
            case 2 -> new Mino_Square();
            case 3 -> new Mino_Bar();
            case 4 -> new Mino_T();
            case 5 -> new Mino_Z1();
            case 6 -> new Mino_Z2();
            default -> mino;
        };
        return mino;
    }

    public void update() {
        // kiểm tra xem tiến trình có hoạt động không
        if(!KeyHandler.gamestart)
        {

        }
        else{
            if (!currentMino.active) {
                // nếu không hoạt động thì thêm vào khối tĩnh
                staticBlocks.add(currentMino.b[0]);
                staticBlocks.add(currentMino.b[1]);
                staticBlocks.add(currentMino.b[2]);
                staticBlocks.add(currentMino.b[3]);

                // game over check
                if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                // Mino hiện tại đã va chạm ngay vào 1 khối và không thể di 
                // chuyển được nữa. nên x và y của nó giống với mino tiếp theo
                // không còn khoảng trống nên gameOver
                    gameOver = true;
                    GamePanel.music.stop();
                    GamePanel.se.play(2,false);
                }

                currentMino.deactivating = false; // reset
                // thay thế mino hiện tại bằng Mino tiếp theo

                currentMino = nextMino;
                currentMino.setXY(MINO_START_X, MINO_START_Y);
                nextMino = minoPicker();
                nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);
                // kiểm tra xem mino có không hoạt động không và kiểm tra xem 
                // dòng có thể xóa được không
                checkDeleteLine();
                checkDeleteColumn();
            } else {
                currentMino.update();
            }
        }
    }

    private void checkDeleteLine() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount =0;

        // vì vậy số blocks tối đa là 12 nếu một dòng có 12 blocks ta có thể xóa
        // dòng
        while (x < right_x && y < bottom_y) {

            for (Block block : PlayManager.staticBlocks) { // scanning
                if (block.x == x && block.y == y) {
                    // tăng số lượng
                    blockCount++;

                }
            }

            x += Block.SIZE;
            if (x == right_x) {

                // dòng đã được lấp đầy và có thể xóa
                if (blockCount == 12) {

                    effectsCounterLOn = true;
                    effectY.add(y);
                    int n = staticBlocks.size() -1;

                    for (int i = n; i > -1; i--) {
                        // xóa tất cả blocks trong dòng y hiện tại
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }

                    }
                    lineCount++;
                    lines++;
                    // để tăng cấp độ + tốc độ sau mỗi 10 dòng

                    if(lines % 10 == 0 &&  dropInterval >1){
                        level++;
                        if(dropInterval >10){
                            dropInterval -=10;
                        }
                        else {
                            dropInterval -=1;
                        }
                    }

                  // Một dòng đã bị xóa nên cần phải di chuyển xuống các blocks
                    for (Block staticBlock : staticBlocks) {

                        // nếu một khối nằm trên y hiện tại, hãy di chuyển nó 
                        // xuống theo kích thước khối

                        if (staticBlock.y < y) {
                            staticBlock.y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0; // reset khi x đạt đến x bên phải vì nó đi 
                                //đến hàng tiếp theo
                x = left_x;
                y += Block.SIZE;
            }
        }
        if(lineCount>0){
            GamePanel.se.play(1,false);
            int singleLineScore = 10 * level;
            score+=singleLineScore * lineCount;
        }
    }
    
    private void checkDeleteColumn() {

        int x = left_x;
        int y = bottom_y - 5*Block.SIZE;
        int blockCount = 0;
        int ColumnCount =0;

        // vì vậy số blocks tối đa là 5 nếu một cột có 5 blocks ta có thể xóa
        // cột
        while (x < right_x && y < bottom_y) {

            for (Block block : PlayManager.staticBlocks) { // scanning
                if (block.x == x && block.y == y) {
                    // tăng số lượng
                    blockCount++;

                }
            }

            y += Block.SIZE;
            if (y == bottom_y) {

                // dòng đã được lấp đầy và có thể xóa
                if (blockCount == 5) {

                    effectsCounterCOn = true;
                    effectX.add(x);
                    int n = staticBlocks.size() -1;
                    
                    for (int i = n; i > -1; i--) {
                        // xóa 5 blocks trong cột x hiện tại
                        if (staticBlocks.get(i).x == x && staticBlocks.get(i).y >= y - 5*Block.SIZE) {
                            staticBlocks.remove(i);
                        }

                    }
                    ColumnCount++;
                    column++;
                    // để tăng cấp độ + tốc độ sau mỗi 10 dòng

                    if(column % 20 == 0 &&  dropInterval >1){
                        level++;
                        if(dropInterval >10){
                            dropInterval -=10;
                        }
                        else {
                            dropInterval -=1;
                        }
                    }

                  // Một dòng đã bị xóa nên cần phải di chuyển xuống các blocks
                    for (Block staticBlock : staticBlocks) {

                        // nếu một khối nằm trên y hiện tại, hãy di chuyển nó 
                        // xuống theo kích thước khối

                        if (staticBlock.y < y && staticBlock.x == x) {
                            staticBlock.y += 5*Block.SIZE;
                        }
                    }
                }
                blockCount = 0; // reset khi x đạt đến x bên phải vì nó đi 
                                //đến hàng tiếp theo
                y = bottom_y - 5*Block.SIZE;
                x += Block.SIZE;
            }
        }
        if(ColumnCount>0){
            GamePanel.se.play(1,false);
            int singleLineScore = 5 * level;
            score+=singleLineScore * ColumnCount;
        }
    }

    public void draw(Graphics2D g2) {
        // Bật anti-aliasing để hiển thị mượt mà hơn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền chuyển màu cho khu vực chơi
        GradientPaint playBG = new GradientPaint(left_x, top_y, Color.GRAY, right_x, bottom_y, Color.DARK_GRAY);
        g2.setPaint(playBG);
        g2.fillRect(left_x, top_y, WIDTH, HEIGHT);

        // Vẽ khung khu vui chơi với đường viền trắng sạch sẽ
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 2, WIDTH + 8, HEIGHT + 8);

        // Bảng Next Mino
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("Next", x + 70, y + 60);

        // Bảng điểm
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString("Level : " + level, x, y);
        y += 70;
        g2.drawString("Lines : " + lines, x, y);
        y += 70;
        g2.drawString("Score : " + score, x, y);

        // Vẽ mino hiện tại, mino tiếp theo và các khối tĩnh như trước
        if (currentMino != null) {
            currentMino.draw(g2);
        }
        nextMino.draw(g2);
        for (Block staticBlock : staticBlocks) {
            staticBlock.draw(g2);
        }

        // Vẽ hiệu ứng nào (xóa dòng, tăng điểm)
        if (effectsCounterLOn) {
            effectCounterL++;
            g2.setColor(Color.RED);
            for (Integer integer : effectY) {
                g2.fillRect(left_x, integer, WIDTH, Block.SIZE);
            }
            if (effectCounterL == 10) {
                effectsCounterLOn = false;
                effectCounterL = 0;
                effectY.clear();
            }
        }
        
        // Vẽ hiệu ứng nào (xóa cột, tăng điểm)
        if (effectsCounterCOn) {
            effectCounterC++;
            g2.setColor(Color.RED);
            for (Integer integer : effectX) {
                g2.fillRect(integer, bottom_y-5*Block.SIZE, Block.SIZE, 5*Block.SIZE);
            }
            if (effectCounterC == 10) {
                effectsCounterCOn = false;
                effectCounterC = 0;
                effectX.clear();
            }
        }

        // Vẽ thông báo PowerUp
        if (GamePanel.powerupInProgress) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("SansSerif", Font.BOLD, 26));
            g2.drawString("Powerup In Progress", 50, 200);
        } else if (GamePanel.powerupused) {
            g2.setColor(Color.ORANGE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 26));
            g2.drawString("Powerup on cooldown", 50, 200);
        } else {
            g2.setColor(Color.CYAN);
            g2.setFont(new Font("SansSerif", Font.BOLD, 26));
            g2.drawString("Press Z to use slowdown!", 50, 200);
        }

        // Vẽ tạm dừng hoặc kết thúc trò chơi
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Verdana", Font.BOLD, 50));
        if (gameOver) {
            x = left_x + (WIDTH / 8);
            y = top_y + (HEIGHT / 2);
            g2.drawString("GAME OVER", x, y);
        } else if (KeyHandler.pausePressed) {
            x = left_x + (WIDTH / 8);
            y = top_y + (HEIGHT / 2);
            g2.drawString("Paused", x, y);
        }

        // Vẽ một tiêu đề trò chơi
        x = 35;
        y = top_y + 50;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 40));
        g2.drawString("Tetris Pro", x, y);

    }
}

