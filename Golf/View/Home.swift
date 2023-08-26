//
//  Home.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 25.08.2023.
//

import SwiftUI
import AVKit

extension UIScreen{
   static let screenWidth = UIScreen.main.bounds.size.width
   static let screenHeight = UIScreen.main.bounds.size.height
   static let screenSize = UIScreen.main.bounds.size
}

struct Home: View {
    
    @StateObject var cameraModel = CameraViewModel()
    
    var body: some View {
        VStack() {
            // Camera view
            CameraView().environmentObject(cameraModel)
            
            // Controls
            HStack() {
                
                Button {
                    
                } label: {
                    Label {
                        
                            Image(systemName: "chevron.right")
                                .font(.callout)
                        
                    } icon:
                    {
                        Text("Preview")
                    }.padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background{
                            Capsule().fill(.black)
                        }
                    
                }.frame(maxHeight: 50, alignment: .bottom)
                    .padding(.bottom, 10)
                    .padding(.bottom, 30)
                Spacer()
                Button {
                    if cameraModel.isRecording {
                        cameraModel.stopRecording()
                    } else {
                        cameraModel.startRecording()
                    }
                } label: {
                        
                        Image(systemName: "figure.golf")
                            .resizable()
                            .renderingMode(/*@START_MENU_TOKEN@*/.template/*@END_MENU_TOKEN@*/)
                            .padding(5)
                            .aspectRatio(contentMode: .fit)
                            .cornerRadius(35)
                            .opacity(cameraModel.isRecording ? 0 : 1)
                            .frame(width: 35, height: 70)
                            .foregroundColor(.white)
                            .background {
                                Circle()
                                    .fill(cameraModel.isRecording ? .green : .black)
                                    .stroke(cameraModel.isRecording ? .green : .white, lineWidth: 3)
                                    .frame(width: 70, height: 70)
                                
                            }
                            .padding(6)
                }
                Spacer()
                Button(){
                    cameraModel.showPreview.toggle()
                } label: {
                    Label 
                    {
                        Image(systemName: "chevron.right")
                            .font(.callout)
                    } icon:
                    {
                        Text("Preview")
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 8)
                    .background{
                        Capsule().fill(.white)
                    }
                }
                
                .frame(maxHeight: 70, alignment: .bottom)
                .padding(.bottom, 10)
                .padding(.bottom, 30)
            }
            .foregroundColor(.black ).padding(2)
            
        }.overlay(content: {
            if let url = cameraModel.previewURL, cameraModel.showPreview {
                FinalPreview(url: url, showPreview: $cameraModel.showPreview)
                    .transition(.move(edge: .trailing))
            }
        })
        .animation(.easeInOut, value: cameraModel.showPreview)
    }
}

struct FinalPreview: View {
    var url: URL
    @Binding var showPreview: Bool
    
    var body: some View {
        GeometryReader{proxy in
            let size = proxy.size
            VideoPlayer(player: AVPlayer(url: url))
                .aspectRatio(contentMode: /*@START_MENU_TOKEN@*/.fill/*@END_MENU_TOKEN@*/)
                .frame(width: size.width, height: size.height)
                .overlay(alignment: .topLeading) {
                    Button {
                        showPreview.toggle()
                    } label: {
                        Label {
                            Text("Back")
                        } icon: {
                            Image(systemName: "chevron.left")
                        }
                    }
                }
        }
    }
}

#Preview {
    Home()
}
