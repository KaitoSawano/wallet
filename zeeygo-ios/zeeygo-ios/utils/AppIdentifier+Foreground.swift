import Foundation

/// This file is **ONLY** for the main ZeeyGo app (foreground process)
///
extension AppIdentifier {
	
	static var current: AppId {
		return .foreground
	}
}

