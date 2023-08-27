//
//  StoreView.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 27.08.2023.
//

import SwiftUI

struct Item: Identifiable {
    var id: Int
    var title: String
    var color: Color
    var img: Image
}

class Store: ObservableObject {
    @Published var items: [Item]
    @State var frames: [Image]
    
    let colors: [Color] = [.red, .orange, .blue, .teal, .mint, .green, .gray, .indigo, .black]

    // dummy data
    init(_frames: [Image]) {
        items = []
        self.frames = _frames
        for (i, j)  in zip(_frames, ["1", "2", "3", "4", "5", "7", "8", "10"]).enumerated() {
            let (img, title) = (j.0, j.1)
            let new = Item(id: i, title: title, color: colors[i], img: img)
            items.append(new)
        }
    }
}


struct StoreView: View {
    
    @State private var snappedItem = 0.0
    @State private var draggingItem = 0.0
    @State var store: Store
    @State var frames: [Image]
    
    init(_frames: [Image]) {
        self.store = Store(_frames: _frames)
        self.snappedItem = 0.0
        self.draggingItem = 0.0
        self.frames = _frames
    }
    
    var body: some View {
            ZStack {
                ForEach(store.items) { item in
                    
                    // article view
                    ZStack {
                        item.img.resizable().frame(width: 300, height: 400).onTapGesture(perform: {
                            /*@START_MENU_TOKEN@*//*@PLACEHOLDER=Code@*/ /*@END_MENU_TOKEN@*/
                        })
                        //                    RoundedRectangle(cornerRadius: 18)
                        //                        .fill(item.color)
                        Text(item.title)
                            .padding(.leading)
                    }
                    .frame(width: 300, height: 400)
                    
                    .scaleEffect(1.0 - abs(distance(item.id)) * 0.2 )
                    .opacity(1.0 - abs(distance(item.id)) * 0.3 )
                    .offset(x: myXOffset(item.id), y: 0)
                    .zIndex(1.0 - abs(distance(item.id)) * 0.1)
                }
            }
            .gesture(
                DragGesture()
                    .onChanged { value in
                        draggingItem = snappedItem + value.translation.width / 100
                    }
                    .onEnded { value in
                        withAnimation {
                            draggingItem = snappedItem + value.predictedEndTranslation.width / 100
                            draggingItem = round(draggingItem).remainder(dividingBy: Double(store.items.count))
                            snappedItem = draggingItem
                        }
                    }
            )
    }
    
    func distance(_ item: Int) -> Double {
        return (draggingItem - Double(item)).remainder(dividingBy: Double($store.items.count))
    }
    
    func myXOffset(_ item: Int) -> Double {
        let angle = Double.pi * 2 / Double($store.items.count) * distance(item)
        return sin(angle) * 200
    }
}
