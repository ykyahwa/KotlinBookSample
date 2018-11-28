package com.tistory.ykyahwa.kotlingithubbooksample.extensions

import io.reactivex.disposables.Disposable

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) = this.add(disposable)