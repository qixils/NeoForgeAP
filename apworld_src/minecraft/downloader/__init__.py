from typing import NamedTuple, Any, Callable
from abc import ABC, abstractmethod

import logging

class ServerInstallData(NamedTuple):
    root_dir: str
    mods_dir: str
    run_args: list[str]


class Step(ABC):
    @abstractmethod
    def run(self,
            context: dict[str, Any],
            *previous: Any,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable[[float, str], None] | None = None,
            error_ok: bool = False):
        pass


class StepsStep(Step):
    def __init__(self, name: str, *steps: Step):
        super().__init__()
        self.steps = steps
        self.logger = logging.Logger("MinecraftClient")
        self.name = name

    def run(self, context: dict[str, Any],
            *previous: Any,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable[[float, str], None] | None = None,
            error_ok: bool = False):
        self._run_index(0, context, *previous, on_success=on_success, on_failure=on_failure, on_progress=on_progress, error_ok=error_ok)
    
    def _run_index(self, index: int,
                   context: dict[str, Any],
                   *previous: Any,
                   on_success: Callable | None = None,
                   on_failure: Callable | None = None,
                   on_progress: Callable[[float, str], None] | None = None,
                   error_ok: bool = False):

        self.logger.debug(f"running step {index}; {type(self)}")

        if len(self.steps) <= index:
            if on_progress is not None:
                on_progress(1.0, self.name)
            if on_success is not None:
                on_success(*previous)
            return
        if on_progress is not None:
            on_progress(index / len(self.steps), self.name)
        success_lambda = lambda *result: self._run_index(index + 1,
                                                         context,
                                                         *result if type(result) is tuple else result,
                                                         on_success=on_success,
                                                         on_failure=on_failure,
                                                         on_progress=on_progress,
                                                         error_ok=error_ok)
        try:
            self.steps[index].run(
                context,
                *previous,
                on_success=success_lambda,
                on_failure=on_failure if not error_ok else (lambda err: success_lambda(*previous)),
                on_progress=lambda value, name: self._emit_on_progress(index=index, extra=value, name=name, on_progress=on_progress),
                error_ok=error_ok,
            )
        except Exception as e:
            self.logger.error("Exception while performing step", exc_info=True)
            if error_ok:
                success_lambda(*previous)
            elif on_failure is not None:
                on_failure(e)
    
    def _emit_on_progress(self, index: int, name: str, extra: float = 0, on_progress: Callable[[float, str], None] | None = None):
        if on_progress is None:
            return

        if extra is None:
            extra = 0
        else:
            extra = max(0.0, min(1.0, extra))

        on_progress((index / len(self.steps)) + (extra / len(self.steps)), name or self.steps[index].name or self.name)




class SyncStep(Step):
    def __init__(self, fn: Callable):
        super().__init__()
        self.fn = fn
        self.logger = logging.Logger("MinecraftClient")

    def run(self,
            context: dict[str, Any],
            *previous: Any,
            on_success: Callable | None = None,
            on_failure: Callable | None = None,
            on_progress: Callable[[float, str], None] | None = None,
            error_ok: bool = False):
        try:
            res = self.fn(context, *previous if type(previous) is tuple else previous)
            self.logger.info(f"Got result {res}")
            if on_success is not None:
                if res is None or type(res) is not tuple:
                    on_success(res)
                else:
                    on_success(*res)
        except Exception as e:
            self.logger.error("Step failed", exc_info=True)
            if on_failure is not None:
                on_failure(e)

class BytesToStringStep(SyncStep):
    def __init__(self):
        super().__init__(BytesToStringStep.bytes_to_string)


    @staticmethod
    def bytes_to_string(context: dict[str, Any], data: bytes):
        return data.decode('utf-8')