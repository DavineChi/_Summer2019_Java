package edu.metrostate.ICS462.assignment3;

import java.io.File;
import java.io.FileWriter;

/****************************************************************************************************************
* ICS 462 SUMMER 2019
* Programming Assignment 3
* Shannon Fisher
* 06/04/2019
* 
* This program expands on the producer consumer problem by adding a fixed-size buffer,
* shared between the producer and consumer threads.
****************************************************************************************************************/

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

public class ProducerConsumer {

	private static final String OUTPUT_FILENAME = "Fisher_Shannon_ProgAssign3.txt";
	
	private static final int MIN_WAIT_PRODUCER = 1000;
	private static final int MAX_WAIT_PRODUCER = 5000;
	
	private static final int MIN_WAIT_CONSUMER = 2000;
	private static final int MAX_WAIT_CONSUMER = 4000;
	
	private static final int WAIT_OTHER_THREAD = 1000;
	
	private int[] data;
	private int producerIndex;
	private int consumerIndex;
	private int complete;
	
	private StringBuilder stringBuilder;
	
	/************************************************************************************************************
	 * Constructor used to create a new ProducerConsumer object to illustrate the producer comsumer problem
	 * in thread concurrency.
	 * <p>
	 * 
	 * @postcondition
	 *   A new ProducerConsumer object has been created.
	 */
	public ProducerConsumer() {
		
		this.data = new int[5];
		this.producerIndex = 0;  // Points to the last item placed in the buffer by the producer.
		this.consumerIndex = 0;  // Points to the last item removed from the buffer by the consumer.
		this.complete = 0;
		
		this.stringBuilder = new StringBuilder();
		
		initStringBuilder();
	}
	
	// Private helper method to populate the assignment output header.
	private void initStringBuilder() {
		
		stringBuilder.append("Shannon Fisher" + "\n");
		stringBuilder.append("ICS 462 Programming Assignment 3" + "\n\n");
	}
	
	/************************************************************************************************************
	 * Method to produce data and store it in a shared resource memory location.
	 * <p>
	 * 
	 * @postcondition
	 *   The shared resource has data written to it.
	 * 
	 * @throws InterruptedException
	 *   InterruptedException is thrown if a thread is interrupted before or during its activity.
	 */
	public void produce() throws InterruptedException {
		
		for (int i = 0; i < 100; i++) {
			
			while (producerIndex != consumerIndex) {
				
				Thread.sleep(ThreadLocalRandom.current().nextInt(WAIT_OTHER_THREAD, WAIT_OTHER_THREAD + 1));
			}
			
			Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_WAIT_PRODUCER, MAX_WAIT_PRODUCER + 1));
			
			data[producerIndex] = i;
			producerIndex = Math.floorMod((producerIndex + 1), data.length);
		}
		
		complete = 1;
	}
	
	/************************************************************************************************************
	 * Method to consume the data stored in the shared resource memory location.
	 * <p>
	 * 
	 * @postcondition
	 *   The data in the shared resource has been read.
	 * 
	 * @throws InterruptedException
	 *   InterruptedException is thrown if a thread is interrupted before or during its activity.
	 */
	public void consume() throws InterruptedException {
		
		while (complete == 0) {
			
			while (producerIndex == consumerIndex) {
				
				Thread.sleep(ThreadLocalRandom.current().nextInt(WAIT_OTHER_THREAD, WAIT_OTHER_THREAD + 1));
				stringBuilder.append("Consumer waiting...\n");
			}
			
			Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_WAIT_CONSUMER, MAX_WAIT_CONSUMER + 1));
			stringBuilder.append(data[consumerIndex] + "\n");
			
			consumerIndex = Math.floorMod((consumerIndex + 1), data.length);
		}
		
		stringBuilder.append("Consumer done.");
	}
	
	/************************************************************************************************************
	 * Method to write the program results to a file.
	 * <p>
	 * 
	 * @postcondition
	 *   The results of the shared resource consumption are output to a file on local storage.
	 * 
	 * @throws IOException
	 *   IOException is thrown if there is an I/O problem with the results file output.
	 */
	public void writeToFile() throws IOException {
		
		// File printing and output setup.
		File file = new File(OUTPUT_FILENAME);
		FileWriter fileWriter = new FileWriter(file, true);
		PrintWriter printWriter = null;
		
		try {
			
			if (!file.exists()) {
				
				file.createNewFile();
			}
			
			printWriter = new PrintWriter(fileWriter);
			
			printWriter.println(stringBuilder.toString());
		}
		
		catch (IOException ex) {
			
			ex.printStackTrace();
		}
		
		finally {
			
			printWriter.close();
		}
	}
	
	/************************************************************************************************************
	 * Main method from where program execution begins.
	 * <p>
	 * 
	 * @param args
	 *   this parameter is not used
	 * 
	 * @postcondition
	 *   The producer consumer thread processes have both performed executions in their own threads.
	 * 
	 * @throws InterruptedException
	 *   InterruptedException is thrown if a thread is interrupted before or during its activity.
	 * 
	 * @throws IOException
	 *   IOException is thrown if there is an I/O problem with the results file output.
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		
		ProducerConsumer pc = new ProducerConsumer();
		
		// Create a new thread for the producer.
		Thread producerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					
					// Attempt the execute the producer method in its own thread.
					pc.produce();
				}
				
				catch (Exception ex) {
					
					ex.printStackTrace();
				}
			}
		});
		
		// Create a new thread for the consumer.
		Thread consumerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					
					// Attempt the execute the consumer method in its own thread.
					pc.consume();
				}
				
				catch (Exception ex) {
					
					ex.printStackTrace();
				}
			}
		});
		
		
		// Start both threads. The order in which the threads actually start is not guaranteed.
		producerThread.start();
		consumerThread.start();
		
		// Join both threads back into a single execution thread.
		producerThread.join();
		consumerThread.join();
		
		// Write the assignment information and program results to a text file.
		// This file is located in the Java project's root folder.
		pc.writeToFile();
	}
}
